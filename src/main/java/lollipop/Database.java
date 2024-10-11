package lollipop;

import discorddb.sqlitedb.*;
import lollipop.commands.leaderboard.models.LBMember;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Static database class to manage all of lollipop's databases
 */
public class Database {

    private static DatabaseTable currency;

    /**
     * Setup and Initialize all the databases
     */
    public static void setupDatabases() {
        try {
//            SQLDatabase.dropTable("currency");
            SQLDatabase.createTable("currency", "id varchar(32) primary key", "lollipops int");
            currency = SQLDatabase.getTable("currency");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get user's current balance
     * @param id user id
     * @return balance amount
     */
    public static int getUserBalance(String id) {
        try {
            Object[][] query = currency.searchQuery("id", "'" + id + "'", "lollipops");
            if (query.length == 0) {
                currency.insertQuery("'" + id + "'", "0");
                return 0;
            }
            return (int)query[0][0];
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Gets a users ranking in terms of lollipops in the specified guild
     * @param id user id
     * @param guild guild to rank
     * @return int array: 1st element = guild rank, 2nd element = global rank
     */
    public static int[] getUserRank(String id, Guild guild) {
        try {
            ArrayList<Integer> guildRank = new ArrayList<>();
            for (Member member : guild.getMembers()) {
                int lp = getUserBalance(member.getId());
                guildRank.add(lp);
            }
            guildRank.sort(Collections.reverseOrder());
            ArrayList<Integer> globalRank = Arrays.stream(
                            Arrays.stream(currency.getColumns("lollipops"))
                                    .map(a -> a[0])
                                    .toArray()
                    )
                    .mapToInt(i -> (int) i)
                    .boxed()
                    .sorted(Collections.reverseOrder())
                    .collect(Collectors.toCollection(ArrayList::new));
            int userLp = getUserBalance(id);
            return new int[]{
                    Collections.binarySearch(guildRank, userLp, Collections.reverseOrder()) + 1,
                    Collections.binarySearch(globalRank, userLp, Collections.reverseOrder()) + 1
            };
        } catch (SQLException e) { e.printStackTrace(); }
        return new int[]{-1, -1};
    }

    /**
     * Gets a users ranking in terms of lollipops in the specified guild
     * @param id user id
     * @param guild guild to rank
     * @return int array: 1st element = guild rank, 2nd element = global rank
     */
    public static int getUserGuildRank(String id, Guild guild) {
        ArrayList<Integer> guildRank = new ArrayList<>();
        for(Member member : guild.getMembers()) {
            int lp = getUserBalance(member.getId());
            guildRank.add(lp);
        }
        guildRank.sort(Collections.reverseOrder());
        int userLp = getUserBalance(id);
        return Collections.binarySearch(guildRank, userLp, Collections.reverseOrder())+1;
    }

    /**
     * Gets a users ranking in terms of lollipops in the specified guild
     * @param id user id
     * @return int array: 1st element = guild rank, 2nd element = global rank
     */
    public static int getUserGlobalRank(String id) {
        try {
            ArrayList<Integer> globalRank = Arrays.stream(
                            Arrays.stream(currency.getColumns("lollipops"))
                                    .map(a -> a[0])
                                    .toArray()
                    )
                    .mapToInt(i -> (int) i)
                    .boxed()
                    .sorted(Collections.reverseOrder())
                    .collect(Collectors.toCollection(ArrayList::new));
            int userLp = getUserBalance(id);
            return Collections.binarySearch(globalRank, userLp, Collections.reverseOrder()) + 1;
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    /**
     * Increment user's balance by specified amount
     * @param id user id
     * @param increment increment amount
     */
    public static void addToUserBalance(String id, int increment) {
        int balance = getUserBalance(id) + increment;
        currency.updateQuery("id", "'" + id + "'", "lollipops="+Math.max(0, balance));
    }

    /**
     * Gets the top ranked members in a guild for lollipops
     * @param guild mentioned guild
     * @return arraylist of {@link LBMember} for the leaderboard
     */
    public static List<List<LBMember>> getLeaderboard(Guild guild) {
        ArrayList<LBMember> result = new ArrayList<>();
        HashMap<String, Integer> userToLollipops = new HashMap<>();
        for(Member member : guild.getMembers()) userToLollipops.put(member.getId(), getUserBalance(member.getId()));
        userToLollipops = Tools.sortByValue(userToLollipops);
        int rank = 0;
        for(String id : userToLollipops.keySet()) {
            Member member = guild.getMemberById(id);
            if(member == null || member.getUser().isBot()) continue;
            result.add(new LBMember(++rank, member.getUser().getName(), userToLollipops.get(id)));
        }
        return IntStream.range(0, result.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / 10))
                .values()
                .stream()
                .map(indices -> indices.stream().map(result::get).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Gets the top ranked members for lollipops globally
     * @param jda current jda instance
     * @return arraylist of {@link LBMember} for the leaderboard
     */
    public static List<List<LBMember>> getLeaderboard(JDA jda) {
        try {
            ArrayList<LBMember> result = new ArrayList<>();
            HashMap<String, Integer> userToLollipops = new HashMap<>();
            for (Object[] row : currency.getColumns("id", "lollipops")) {
                String ID = (String) row[0];
                int lollipops = (int) row[1];
                userToLollipops.put(ID, lollipops);
            }
            userToLollipops = Tools.sortByValue(userToLollipops);
            int rank = 0;
            for(String id : userToLollipops.keySet()) {
                User user = jda.getShardManager().getUserById(id);
                if(user == null || user.isBot()) continue;
                result.add(new LBMember(++rank, user.getName(), userToLollipops.get(id)));
            }
            return IntStream.range(0, Math.min(result.size(), 100))
                    .boxed()
                    .collect(Collectors.groupingBy(i -> i / 10))
                    .values()
                    .stream()
                    .map(indices -> indices.stream().map(result::get).collect(Collectors.toList()))
                    .collect(Collectors.toList());
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Gets the amount of keys in the currency database
     * @return integer for number of users in currency database
     */
    public static int getCurrencyUserCount() {
        try {
            ResultSet result = SQLDatabase.executeQuery("SELECT COUNT(*) FROM currency");
            return result.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static void migrateFromJson(List<JDA> jdas) {
        try {
            DataObject jsonDb = DataObject.fromJson(new FileReader(".files/currency.json")).getObject("data");
            for(String id : jsonDb.keys())
                currency.insertQuery("'" + id + "'", Integer.toString(jsonDb.getInt(id)));
            List<String> ids = jsonDb.keys().stream().toList();
            for(JDA jda : jdas) {
                for(int i=0; i<300; i++) {
                    int random = (int)(Math.random() * ids.size());
                    User user = jda.getUserById(ids.get(random));
                    while(user == null || user.isBot()) {
                        random = (int)(Math.random() * ids.size());
                        user = jda.getUserById(ids.get(random));
                    }
                    int lollipopCount = getUserBalance(ids.get(random)) + (int)(Math.random() * 50);
                    currency.updateQuery("id", "'" + ids.get(random) + "'", "lollipops=" + lollipopCount);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
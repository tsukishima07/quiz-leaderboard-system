import java.net.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

public class QuizApp {

    static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";

    public static void main(String[] args) throws Exception {

        String regNo = "RA2311003020069";

        Map<String, Integer> scores = new HashMap<>();
        Set<String> seen = new HashSet<>();

        Gson gson = new Gson();

        for (int i = 0; i < 10; i++) {

            int attempts = 0;
            boolean success = false;

            while (attempts < 3 && !success) {
                try {
                    String urlStr = BASE_URL + "/quiz/messages?regNo=" + regNo + "&poll=" + i;
                    URL url = new URL(urlStr);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int status = conn.getResponseCode();

                    if (status != 200) {
                        System.out.println("Poll " + i + " failed (" + status + "), retrying...");
                        attempts++;
                        Thread.sleep(2000);
                        continue;
                    }

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    JsonObject json = gson.fromJson(response.toString(), JsonObject.class);
                    JsonArray events = json.getAsJsonArray("events");

                    for (JsonElement e : events) {
                        JsonObject obj = e.getAsJsonObject();

                        String roundId = obj.get("roundId").getAsString();
                        String participant = obj.get("participant").getAsString();
                        int score = obj.get("score").getAsInt();

                        String key = roundId + "_" + participant;

                        if (!seen.contains(key)) {
                            seen.add(key);
                            scores.put(participant,
                                    scores.getOrDefault(participant, 0) + score);
                        }
                    }

                    success = true;

                } catch (Exception e) {
                    System.out.println("Retrying poll " + i + "...");
                    attempts++;
                    Thread.sleep(2000);
                }
            }

            if (success) {
                System.out.println("Poll " + i + " done");
            } else {
                System.out.println("Skipping poll " + i);
            }

            Thread.sleep(5000); // required delay
        }

        // Sort leaderboard
        List<Map.Entry<String, Integer>> list = new ArrayList<>(scores.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());

        int total = 0;

        System.out.println("\nLeaderboard:");
        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
            total += entry.getValue();
        }

        System.out.println("\nTotal Score = " + total);

        // Submit result
        submit(regNo, list);
    }

    public static void submit(String regNo, List<Map.Entry<String, Integer>> list) throws Exception {

        URL url = new URL(BASE_URL + "/quiz/submit");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        StringBuilder json = new StringBuilder();
        json.append("{\"regNo\":\"").append(regNo).append("\",");
        json.append("\"leaderboard\":[");

        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, Integer> e = list.get(i);

            json.append("{\"participant\":\"")
                    .append(e.getKey())
                    .append("\",\"totalScore\":")
                    .append(e.getValue())
                    .append("}");

            if (i < list.size() - 1)
                json.append(",");
        }

        json.append("]}");

        OutputStream os = conn.getOutputStream();
        os.write(json.toString().getBytes());
        os.flush();
        os.close();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        reader.close();
    }
}
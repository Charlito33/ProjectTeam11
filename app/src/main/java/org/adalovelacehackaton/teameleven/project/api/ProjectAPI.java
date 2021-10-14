package org.adalovelacehackaton.teameleven.project.api;

import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProjectAPI {
    private static final String BASE_URL = "https://charlito33.fr.nf/team11/project";
    private static final String API_KEY = "49d651fd7e0d504127da936da405fc15ab5864c3250401b74642237b4f29ddc0eaf16a0457476e92ced78c0d580942f7eda3737233aeb53bbe6c0ac63c1cdd9e";

    public static void loginUser(String username, String password, LoginExecutor executor) {
        try {
            URL url = new URL(BASE_URL + "/api/login.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("api_key", API_KEY);
            con.setRequestProperty("username", username);
            con.setRequestProperty("password", password);

            Thread t = new Thread(() -> {
                Looper.prepare();

                try {
                    int responseCode = con.getResponseCode();

                    String data;

                    if (responseCode == 200) {
                        data = readInputStream(con.getInputStream());
                    } else {
                        data = readInputStream(con.getErrorStream());
                    }

                    System.out.println("Got API response for \"login\" : " + responseCode);
                    System.out.println("Got Data : " + data);

                    executor.execute(responseCode, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        } catch (IOException e) {
            System.err.println("Can't connect to API !");
            e.printStackTrace();
        }
    }

    public static void getUser(String accessToken, UserDataExecutor executor) {
        try {
            URL url = new URL(BASE_URL + "/api/userdata.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("api_key", API_KEY);
            con.setRequestProperty("access_token", accessToken);

            Thread t = new Thread(() -> {
                Looper.prepare();

                try {
                    int responseCode = con.getResponseCode();

                    String data;
                    User user = null;

                    if (responseCode == 200) {
                        data = readInputStream(con.getInputStream());
                        user = new User(data);
                    } else {
                        data = readInputStream(con.getErrorStream());
                    }

                    System.out.println("Got API response for \"getUser\" : " + responseCode);
                    System.out.println("Got Data : " + data);

                    executor.execute(responseCode, data, user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        } catch (IOException e) {
            System.err.println("Can't connect to API !");
            e.printStackTrace();
        }
    }

    private static String readInputStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            output = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                output.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Can't read InputStream \"" + inputStream + "\" !");
            e.printStackTrace();
        }

        return output.toString();
    }
}

package org.adalovelacehackaton.teameleven.ecoscan.api;

import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProjectAPI {
    private static final String BASE_URL = "https://charlito33.fr.nf/team11/ecoscan";
    private static final String API_KEY = "5d84e64c02198669cfdfb9d6625e580e754c14da2129266ca7dfb99d64a7d792";

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

    public static void registerUser(String username, String email, String firstname, String lastname, String password, String passwordConfirm, RegisterExecutor executor) {
        try {
            URL url = new URL(BASE_URL + "/api/register.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("api_key", API_KEY);
            con.setRequestProperty("username", username);
            con.setRequestProperty("email", email);
            con.setRequestProperty("firstname", firstname);
            con.setRequestProperty("lastname", lastname);
            con.setRequestProperty("password", password);
            con.setRequestProperty("password_confirm", passwordConfirm);

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

                    System.out.println("Got API response for \"register\" : " + responseCode);
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

    public static void getItem(String scancode, ItemExecutor executor) {
        try {
            URL url = new URL(BASE_URL + "/api/get_item.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("api_key", API_KEY);
            con.setRequestProperty("scancode", scancode);

            Thread t = new Thread(() -> {
                Looper.prepare();

                try {
                    int responseCode = con.getResponseCode();

                    String data;
                    Item item = null;

                    if (responseCode == 200) {
                        data = readInputStream(con.getInputStream());
                        item = new Item(data);
                    } else {
                        data = readInputStream(con.getErrorStream());
                    }

                    System.out.println("Got API response for \"getItem\" : " + responseCode);
                    System.out.println("Got Data : " + data);

                    executor.execute(responseCode, data, item);
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

    public static void addItem(Item item, ItemAddExecutor executor) {
        try {
            URL url = new URL(BASE_URL + "/api/add_item.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("api_key", API_KEY);
            con.setRequestProperty("scancode", item.getScancode());
            con.setRequestProperty("name", item.getName());
            con.setRequestProperty("type", item.getType().getName());
            con.setRequestProperty("weight", String.valueOf(item.getWeight()));

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

                    System.out.println("Got API response for \"addItem\" : " + responseCode);
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

    public static void logItemToUserAccount(String accessToken, Item item) {
        try {
            URL url = new URL(BASE_URL + "/api/user_add_item.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("api_key", API_KEY);
            con.setRequestProperty("access_token", accessToken);
            con.setRequestProperty("scancode", item.getScancode());

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

                    System.out.println("Got API response for \"addItem\" : " + responseCode);
                    System.out.println("Got Data : " + data);
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

    public static void getUserItemsCount(String accessToken, UserItemsCountExecutor executor) {
        try {
            URL url = new URL(BASE_URL + "/api/get_user_items_count.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("api_key", API_KEY);
            con.setRequestProperty("access_token", accessToken);

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

                    System.out.println("Got API response for \"addItem\" : " + responseCode);
                    System.out.println("Got Data : " + data);

                    executor.execute(responseCode, data, new JSONObject(data));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        } catch (IOException e) {
            System.err.println("Can't connect to API !");
            e.printStackTrace();
        }
    }

    public static void getUserRanking(String accessToken, RankingExecutor executor) {
        try {
            URL url = new URL(BASE_URL + "/api/get_user_ranking.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty("api_key", API_KEY);
            con.setRequestProperty("access_token", accessToken);

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

                    System.out.println("Got API response for \"getUserRanking\" : " + responseCode);
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

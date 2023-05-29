package com.example.fitmatch.utilities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.fitmatch.R;
import com.example.fitmatch.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Notifications extends Worker {

    private final Context context;
    private ActivityResultLauncher<String> requestLauncher;


    public Notifications(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {
        getRandomQuotesFromAPI();
        return Result.success();
    }


    private void getRandomQuotesFromAPI() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://api.api-ninjas.com/v1/quotes?category=inspirational";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Display the first 500 characters of the response string.

                        String quote = "", author = "";

                        for (int i = 0; i < response.length(); i++) {
                            // creating a new json object and
                            // getting each object from our json array.
                            try {
                                // we are getting each json object.
                                JSONObject responseObj = response.getJSONObject(i);

                                quote = responseObj.getString("quote");
                                author = responseObj.getString("author");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        showNotification(quote, author);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
                System.out.println(error.toString());
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap header = new HashMap();
                header.put("X-Api-Key", "w64eQQv2AkYVVktJIpWHAA==TZKT6D5xIWfemSub");
                return header;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);


        // La requête est terminée, vous pouvez envoyer une notification ici


    }

    @SuppressLint("MissingPermission")
    private void showNotification(String quote, String author) {
        createNotificationChannel();

        String description = quote + " " + author;
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Quotes")
                .setSmallIcon(R.drawable.logofitmach)
                .setContentTitle("Fit Match")
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Quotes";
            String channelName = "Quotes Channel";
            String channelDescription = "Channel for Quotes";

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void setNotification(Context context) {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(Notifications.class, 6, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(context).enqueue(periodicWorkRequest);
    }

}

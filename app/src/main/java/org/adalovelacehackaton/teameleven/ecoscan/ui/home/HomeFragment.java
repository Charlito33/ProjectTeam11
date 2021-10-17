package org.adalovelacehackaton.teameleven.ecoscan.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.adalovelacehackaton.teameleven.ecoscan.BarcodeActivity;
import org.adalovelacehackaton.teameleven.ecoscan.LoginActivity;
import org.adalovelacehackaton.teameleven.ecoscan.R;
import org.adalovelacehackaton.teameleven.ecoscan.api.ProjectAPI;
import org.adalovelacehackaton.teameleven.ecoscan.databinding.FragmentHomeBinding;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private SharedPreferences sharedPreferences;

    private String accessToken;

    // PieChart
    private TextView tvBioDegradable, tvCardboardPaper, tvOther, tvGlass, tvPlastic, tvTextile, tvMetal;
    private PieChart pieChart;

    private int bioDegradablePercent = 10,
                    cardboardPaperPercent = 10,
                    otherPercent = 10,
                    glassPercent = 10,
                    plasticPercent = 10,
                    textilePercent = 10,
                    metalPercent = 10;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Activity != null check
        if (getActivity() == null) {
            System.err.println("Error when initializing Fragment !");
            Toast.makeText(getContext(), "Fragment Error", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Init Shared Preferences with Global Value
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);

        // Check access_token (And by the way username...)
        accessToken = sharedPreferences.getString("access_token", "null");
        if (accessToken.equals("null")) {
            // ReLogin
            connect();
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // Link objects
        /*
        tvBioDegradable =       v.findViewById(R.id.tvBioDegradable);
        tvCardboardPaper =      v.findViewById(R.id.tvCardboardPaper);
        tvOther =               v.findViewById(R.id.tvOther);
        tvGlass =               v.findViewById(R.id.tvGlass);
        tvPlastic =             v.findViewById(R.id.tvPlastic);
        tvTextile =             v.findViewById(R.id.tvTextile);
        tvMetal =               v.findViewById(R.id.tvMetal);

         */

        pieChart = v.findViewById(R.id.piechart);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        createListeners();

        // View != null check
        if (getView() == null) {
            System.err.println("Error when initializing Fragment !");
            Toast.makeText(getContext(), "Fragment Error", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get values
        ProjectAPI.getUser(accessToken, (responseCode, data, user) -> {
            if (responseCode == 200) {
                TextView textViewPoints = getView().findViewById(R.id.textView_ranking);
                textViewPoints.post(() -> {
                    textViewPoints.setText(String.valueOf(user.getPoints()));
                });

            } else {
                System.err.println("Error when trying to get userdata !");
                System.err.println(data);
            }
        });

        setData();
    }

    private void createListeners() {
        FloatingActionButton fab = (FloatingActionButton) binding.getRoot().findViewById(R.id.fab_barcode);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setData() {
        ProjectAPI.getUserItemsCount(accessToken, (responseCode, data, jsonData) -> {
            if (responseCode == 200) {
                try {
                    float totalCount = jsonData.getInt("total_count");

                    float bioDegradableCount = jsonData.getInt("bio_degradable_count");
                    float cardboardPaperCount = jsonData.getInt("cardboard_paper_count");
                    float otherCount = jsonData.getInt("other_count");
                    float plasticCount = jsonData.getInt("plastic_count");
                    float glassCount = jsonData.getInt("glass_count");
                    float textileCount = jsonData.getInt("textile_count");
                    float metalCount = jsonData.getInt("metal_count");

                    bioDegradablePercent = Math.round((bioDegradableCount / totalCount) * 100);
                    cardboardPaperPercent = Math.round((cardboardPaperCount / totalCount) * 100);
                    otherPercent = Math.round((otherCount / totalCount) * 100);
                    plasticPercent = Math.round((plasticCount / totalCount) * 100);
                    glassPercent = Math.round((glassCount / totalCount) * 100);
                    textilePercent = Math.round((textileCount / totalCount) * 100);
                    metalPercent = Math.round((metalCount / totalCount) * 100);

                    pieChart.post(() -> {
                        updateData();
                    });
                } catch (JSONException e) {
                    System.err.println("Can't parse JsonData of user items count !");
                    e.printStackTrace();
                }
            } else {
                System.err.println("Error when trying to get user items count !");
                System.err.println(data);
            }
        });
    }

    private void updateData() {
        Resources.Theme theme = getActivity().getTheme();

        pieChart.setInnerPaddingColor(getResources().getColor(R.color.cardview_background, theme));

        pieChart.clearChart();

        pieChart.addPieSlice(new PieModel(
                        (String) getText(R.string.bio_degradable),
                bioDegradablePercent,
                        getResources().getColor(R.color.bio_degradable_color, theme)
                ));
        pieChart.addPieSlice(new PieModel(
                (String) getText(R.string.cardboard_paper),
                cardboardPaperPercent,
                getResources().getColor(R.color.cardboard_paper_color, theme)
        ));
        pieChart.addPieSlice(new PieModel(
                (String) getText(R.string.other),
                otherPercent,
                getResources().getColor(R.color.other_color, theme)
        ));
        pieChart.addPieSlice(new PieModel(
                (String) getText(R.string.glass),
                glassPercent,
                getResources().getColor(R.color.glass_color, theme)
        ));
        pieChart.addPieSlice(new PieModel(
                (String) getText(R.string.plastic),
                plasticPercent,
                getResources().getColor(R.color.plastic_color, theme)
        ));
        pieChart.addPieSlice(new PieModel(
                (String) getText(R.string.textile),
                textilePercent,
                getResources().getColor(R.color.textile_color, theme)
        ));
        pieChart.addPieSlice(new PieModel(
                (String) getText(R.string.metal),
                metalPercent,
                getResources().getColor(R.color.metal_color, theme)
        ));

        pieChart.startAnimation();
    }

    private void connect() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra("reason", "disconnected");
        startActivity(intent);
    }
}
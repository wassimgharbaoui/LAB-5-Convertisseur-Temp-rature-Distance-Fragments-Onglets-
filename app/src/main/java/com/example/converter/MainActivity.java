package com.example.converter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends Activity {
    private static final int MENU_QUIT = 1;
    private static final int CONTENT_ID = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView placeholder = new TextView(this);
        placeholder.setId(CONTENT_ID);
        setContentView(placeholder, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.addTab(actionBar.newTab()
                    .setText("C <-> F")
                    .setTabListener(new ConverterTabListener("temperature")));
            actionBar.addTab(actionBar.newTab()
                    .setText("KM <-> MILES")
                    .setTabListener(new ConverterTabListener("distance")));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_QUIT, Menu.NONE, R.string.menu_quit)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_QUIT) {
            confirmExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private class ConverterTabListener implements ActionBar.TabListener {
        private final String mode;

        ConverterTabListener(String mode) {
            this.mode = mode;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction transaction) {
            transaction.replace(CONTENT_ID, ConverterFragment.newInstance(mode));
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction transaction) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction transaction) {
        }
    }

    public static class ConverterFragment extends Fragment {
        private static final String ARG_MODE = "mode";
        private final DecimalFormat resultFormat = new DecimalFormat("0.##");

        static ConverterFragment newInstance(String mode) {
            ConverterFragment fragment = new ConverterFragment();
            Bundle args = new Bundle();
            args.putString(ARG_MODE, mode);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            String mode = getArguments() == null ? "temperature" : getArguments().getString(ARG_MODE, "temperature");
            if ("distance".equals(mode)) {
                return createConverterView(
                        "Choisir l'opération :",
                        "KM to Miles",
                        "Miles to KM",
                        "Km",
                        "Miles",
                        value -> value * 0.621371,
                        value -> value / 0.621371
                );
            }

            return createConverterView(
                    "Choisir l'opération :",
                    "C to F",
                    "F to C",
                    "°C",
                    "°F",
                    value -> value * 9 / 5 + 32,
                    value -> (value - 32) * 5 / 9
            );
        }

        private LinearLayout createConverterView(
                String operationLabel,
                String firstChoice,
                String secondChoice,
                String firstUnit,
                String secondUnit,
                Converter firstConversion,
                Converter secondConversion
        ) {
            Context context = getActivity();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(dp(24), dp(32), dp(24), dp(24));
            layout.setBackgroundColor(getResources().getColor(R.color.screen_background));

            TextView operation = new TextView(context);
            operation.setText(operationLabel);
            operation.setTextSize(14);
            layout.addView(operation);

            RadioGroup group = new RadioGroup(context);
            group.setOrientation(RadioGroup.VERTICAL);

            RadioButton first = new RadioButton(context);
            first.setText(firstChoice);
            first.setId(1001);

            RadioButton second = new RadioButton(context);
            second.setText(secondChoice);
            second.setId(1002);
            second.setChecked(true);

            group.addView(first);
            group.addView(second);
            layout.addView(group);

            LinearLayout inputRow = new LinearLayout(context);
            inputRow.setOrientation(LinearLayout.HORIZONTAL);
            inputRow.setGravity(Gravity.CENTER_VERTICAL);
            inputRow.setPadding(0, dp(26), 0, dp(18));

            TextView valueLabel = new TextView(context);
            valueLabel.setText("Valeur à convertir :");
            valueLabel.setTextSize(14);

            EditText input = new EditText(context);
            input.setHint("Saisir la valeur");
            input.setSingleLine(true);
            input.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL
                    | InputType.TYPE_NUMBER_FLAG_SIGNED);

            inputRow.addView(valueLabel);
            inputRow.addView(input, new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            layout.addView(inputRow);

            LinearLayout resultRow = new LinearLayout(context);
            resultRow.setOrientation(LinearLayout.HORIZONTAL);
            resultRow.setGravity(Gravity.CENTER_VERTICAL);

            Button convert = new Button(context);
            convert.setText("OK");

            TextView result = new TextView(context);
            result.setTextSize(16);
            result.setTypeface(Typeface.DEFAULT_BOLD);
            result.setGravity(Gravity.CENTER_VERTICAL);
            result.setPadding(dp(18), 0, 0, 0);

            resultRow.addView(convert, new LinearLayout.LayoutParams(dp(120), dp(48)));
            resultRow.addView(result, new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            layout.addView(resultRow);

            convert.setOnClickListener(view -> {
                hideKeyboard(input);
                String rawValue = input.getText().toString().trim();
                if (rawValue.isEmpty()) {
                    result.setText("");
                    Toast.makeText(context, "Veuillez saisir une valeur.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double value = Double.parseDouble(rawValue);
                    boolean useFirst = group.getCheckedRadioButtonId() == first.getId();
                    double converted = useFirst
                            ? firstConversion.convert(value)
                            : secondConversion.convert(value);
                    String targetUnit = useFirst ? secondUnit : firstUnit;
                    result.setText(resultFormat.format(converted) + " " + targetUnit);
                } catch (NumberFormatException exception) {
                    result.setText("");
                    Toast.makeText(context, "Valeur invalide.", Toast.LENGTH_SHORT).show();
                }
            });

            return layout;
        }

        private void hideKeyboard(EditText input) {
            InputMethodManager manager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        }

        private int dp(int value) {
            return Math.round(value * getResources().getDisplayMetrics().density);
        }
    }

    private interface Converter {
        double convert(double value);
    }
}

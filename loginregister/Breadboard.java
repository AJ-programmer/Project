package com.example.loginregister;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Breadboard extends AppCompatActivity {

    private Spinner gateSpinner;
    private SwitchCompat inputASwitch, inputBSwitch;
    private ImageView ledView;
    private TextView outputText;
    private BreadboardView breadboardView;
    private Button modeButton, resetButton;
    private String selectedGate = "AND";
    private boolean inputA = false;
    private boolean inputB = false;
    private int mode = 0; // 0: None, 1: Place IC, 2: Add Wire, 3: Place LED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_breadboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupGateSelector();
        setupInputSwitches();
        setupModeSelector();
        setupResetButton();
        updateLogic();
    }

    private void initializeViews() {
        gateSpinner = findViewById(R.id.gateSpinner);
        inputASwitch = findViewById(R.id.inputASwitch);
        inputBSwitch = findViewById(R.id.inputBSwitch);
        ledView = findViewById(R.id.ledView);
        outputText = findViewById(R.id.outputText);
        breadboardView = findViewById(R.id.breadboardView);
        modeButton = findViewById(R.id.modeButton);
        resetButton = findViewById(R.id.resetButton);

        breadboardView.setOnTouchListener((v, event) -> {
            if (mode == 1) {
                breadboardView.placeIC((int) event.getX(), (int) event.getY(), selectedGate);
                mode = 0; // Reset mode after placement
                updateModeButton();
                Toast.makeText(this, "IC placed for " + selectedGate, Toast.LENGTH_SHORT).show();
            } else if (mode == 2) {
                breadboardView.addWire((int) event.getX(), (int) event.getY());
                Toast.makeText(this, "Wire added", Toast.LENGTH_SHORT).show();
            } else if (mode == 3) {
                breadboardView.placeLED((int) event.getX(), (int) event.getY());
                mode = 0; // Reset mode after placement
                updateModeButton();
                Toast.makeText(this, "LED placed", Toast.LENGTH_SHORT).show();
            }
            updateLogic();
            return true;
        });
    }

    private void setupGateSelector() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gate_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gateSpinner.setAdapter(adapter);
        gateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGate = parent.getItemAtPosition(position).toString();
                inputBSwitch.setVisibility(selectedGate.equals("NOT") ? View.GONE : View.VISIBLE);
                breadboardView.setGate(selectedGate);
                updateLogic();
                Toast.makeText(Breadboard.this, selectedGate + " gate selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupInputSwitches() {
        inputASwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            inputA = isChecked;
            breadboardView.setInputA(inputA);
            updateLogic();
        });

        inputBSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            inputB = isChecked;
            breadboardView.setInputB(inputB);
            updateLogic();
        });
    }

    private void setupModeSelector() {
        modeButton.setOnClickListener(v -> {
            mode = (mode + 1) % 4;
            updateModeButton();
        });
        updateModeButton(); // Initialize button text
    }

    private void setupResetButton() {
        resetButton.setOnClickListener(v -> {
            resetBreadboard();
        });
    }

    private void resetBreadboard() {
        // Reset all inputs
        inputA = false;
        inputB = false;
        inputASwitch.setChecked(false);
        inputBSwitch.setChecked(false);

        // Reset mode
        mode = 0;
        updateModeButton();

        // Reset gate selection to default
        gateSpinner.setSelection(0);
        selectedGate = "AND";

        // Reset breadboard view (clear all components)
        breadboardView.reset();

        // Update logic and display
        updateLogic();

        Toast.makeText(this, "Breadboard reset!", Toast.LENGTH_SHORT).show();
    }

    private void updateModeButton() {
        String modeText = mode == 0 ? "None" : mode == 1 ? "Place IC" : mode == 2 ? "Add Wire" : "Place LED";
        modeButton.setText("Mode: " + modeText);
        Toast.makeText(this, "Mode: " + modeText, Toast.LENGTH_SHORT).show();
    }

    private void updateLogic() {
        boolean output = calculateOutput();

        // Use system drawables with color filters instead of custom LED drawables
        if (output) {
            ledView.setImageResource(android.R.drawable.presence_online);
            ledView.setColorFilter(Color.GREEN);
        } else {
            ledView.setImageResource(android.R.drawable.presence_offline);
            ledView.setColorFilter(Color.RED);
        }

        outputText.setText("Output: " + (output ? "HIGH (1)" : "LOW (0)"));
        breadboardView.setOutput(output); // Update BreadboardView with output state
    }

    private boolean calculateOutput() {
        // Logic based on current input switches
        switch (selectedGate) {
            case "AND":
                return inputA && inputB;
            case "OR":
                return inputA || inputB;
            case "NOT":
                return !inputA;
            case "NAND":
                return !(inputA && inputB);
            case "NOR":
                return !(inputA || inputB);
            case "XOR":
                return inputA != inputB; // XOR is true when inputs are different
            default:
                return false;
        }
    }
}

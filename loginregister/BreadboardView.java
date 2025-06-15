package com.example.loginregister;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BreadboardView extends View {

    private Paint breadboardPaint;
    private Paint holePaint;
    private Paint icPaint;
    private Paint wirePaint;
    private Paint ledPaint;
    private Paint textPaint;

    private String currentGate = "AND";
    private boolean inputA = false;
    private boolean inputB = false;
    private boolean output = false;

    private List<IC> placedICs;
    private List<Wire> wires;
    private List<LED> leds;
    private List<Connection> connections;

    // Breadboard dimensions and spacing
    private static final int HOLE_SPACING = 40;
    private static final int HOLE_RADIUS = 6;
    private static final int BREADBOARD_ROWS = 15;
    private static final int BREADBOARD_COLS = 20;
    private static final int CONNECTION_THRESHOLD = 30; // Distance threshold for connections

    public BreadboardView(Context context) {
        super(context);
        init();
    }

    public BreadboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BreadboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialize paint objects
        breadboardPaint = new Paint();
        breadboardPaint.setColor(Color.parseColor("#8B4513")); // Brown color for breadboard
        breadboardPaint.setStyle(Paint.Style.FILL);

        holePaint = new Paint();
        holePaint.setColor(Color.BLACK);
        holePaint.setStyle(Paint.Style.FILL);

        icPaint = new Paint();
        icPaint.setColor(Color.GRAY);
        icPaint.setStyle(Paint.Style.FILL);
        icPaint.setAntiAlias(true);

        wirePaint = new Paint();
        wirePaint.setColor(Color.RED);
        wirePaint.setStyle(Paint.Style.STROKE);
        wirePaint.setStrokeWidth(5);
        wirePaint.setAntiAlias(true);

        ledPaint = new Paint();
        ledPaint.setAntiAlias(true);
        ledPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(20);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Initialize lists
        placedICs = new ArrayList<>();
        wires = new ArrayList<>();
        leds = new ArrayList<>();
        connections = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw breadboard background
        canvas.drawRect(0, 0, getWidth(), getHeight(), breadboardPaint);

        // Draw breadboard holes
        drawBreadboardHoles(canvas);

        // Draw placed components
        drawICs(canvas);
        drawWires(canvas);
        drawLEDs(canvas);

        // Draw connection lines (simplified)
        drawConnections(canvas);

        // Draw input/output indicators
        drawIOIndicators(canvas);
    }

    private void drawBreadboardHoles(Canvas canvas) {
        int startX = 50;
        int startY = 50;

        for (int row = 0; row < BREADBOARD_ROWS; row++) {
            for (int col = 0; col < BREADBOARD_COLS; col++) {
                float x = startX + col * HOLE_SPACING;
                float y = startY + row * HOLE_SPACING;
                canvas.drawCircle(x, y, HOLE_RADIUS, holePaint);
            }
        }
    }

    private void drawICs(Canvas canvas) {
        for (IC ic : placedICs) {
            // Draw IC body - color based on activity
            icPaint.setColor(ic.isActive ? Color.parseColor("#4CAF50") : Color.GRAY);
            RectF icRect = new RectF(ic.x - 40, ic.y - 20, ic.x + 40, ic.y + 20);
            canvas.drawRoundRect(icRect, 5, 5, icPaint);

            // Draw IC label
            canvas.drawText(ic.gateType, ic.x, ic.y + 5, textPaint);

            // Draw pins with state colors
            Paint pinPaint = new Paint();
            pinPaint.setStyle(Paint.Style.FILL);

            // Input A pin
            pinPaint.setColor(ic.inputAState ? Color.GREEN : Color.RED);
            canvas.drawCircle(ic.x - 40, ic.y - 10, 4, pinPaint);

            // Input B pin (only for gates that need it)
            if (!ic.gateType.equals("NOT")) {
                pinPaint.setColor(ic.inputBState ? Color.GREEN : Color.RED);
                canvas.drawCircle(ic.x - 40, ic.y + 10, 4, pinPaint);
            }

            // Output pin
            pinPaint.setColor(ic.outputState ? Color.GREEN : Color.RED);
            canvas.drawCircle(ic.x + 40, ic.y, 4, pinPaint);

            // Draw pin labels
            Paint labelPaint = new Paint();
            labelPaint.setColor(Color.BLACK);
            labelPaint.setTextSize(12);
            labelPaint.setAntiAlias(true);
            labelPaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText("A", ic.x - 50, ic.y - 5, labelPaint);
            if (!ic.gateType.equals("NOT")) {
                canvas.drawText("B", ic.x - 50, ic.y + 15, labelPaint);
            }
            canvas.drawText("O", ic.x + 50, ic.y + 5, labelPaint);
        }
    }

    private void drawWires(Canvas canvas) {
        for (Wire wire : wires) {
            wirePaint.setColor(wire.isActive ? Color.GREEN : Color.parseColor("#FF5722"));
            wirePaint.setStrokeWidth(wire.isActive ? 6 : 4);
            canvas.drawLine(wire.startX, wire.startY, wire.endX, wire.endY, wirePaint);

            // Draw connection points
            Paint pointPaint = new Paint();
            pointPaint.setColor(wire.isActive ? Color.GREEN : Color.RED);
            pointPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(wire.startX, wire.startY, 5, pointPaint);
            canvas.drawCircle(wire.endX, wire.endY, 5, pointPaint);
        }
    }

    private void drawLEDs(Canvas canvas) {
        for (LED led : leds) {
            // LED glow effect when on
            if (led.isOn) {
                Paint glowPaint = new Paint();
                glowPaint.setColor(Color.parseColor("#80FF0000")); // Semi-transparent red
                canvas.drawCircle(led.x, led.y, 20, glowPaint);
            }

            ledPaint.setColor(led.isOn ? Color.RED : Color.parseColor("#FFCDD2"));
            canvas.drawCircle(led.x, led.y, 15, ledPaint);

            // Draw LED outline
            Paint outlinePaint = new Paint();
            outlinePaint.setColor(Color.BLACK);
            outlinePaint.setStyle(Paint.Style.STROKE);
            outlinePaint.setStrokeWidth(2);
            canvas.drawCircle(led.x, led.y, 15, outlinePaint);

            // Draw LED pins
            Paint pinPaint = new Paint();
            pinPaint.setColor(led.inputState ? Color.GREEN : Color.RED);
            pinPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(led.x - 15, led.y, 3, pinPaint);
            canvas.drawCircle(led.x + 15, led.y, 3, pinPaint);
        }
    }

    private void drawConnections(Canvas canvas) {
        // Draw simplified power rails
        Paint railPaint = new Paint();
        railPaint.setStrokeWidth(2);
        railPaint.setStyle(Paint.Style.STROKE);

        // Input A rail (top)
        railPaint.setColor(inputA ? Color.GREEN : Color.GRAY);
        canvas.drawLine(20, 80, getWidth() - 20, 80, railPaint);
        canvas.drawText("Input A: " + (inputA ? "HIGH" : "LOW"), 100, 70, textPaint);

        // Input B rail (middle)
        railPaint.setColor(inputB ? Color.GREEN : Color.GRAY);
        canvas.drawLine(20, 120, getWidth() - 20, 120, railPaint);
        canvas.drawText("Input B: " + (inputB ? "HIGH" : "LOW"), 100, 110, textPaint);

        // Output rail (bottom)
        railPaint.setColor(output ? Color.GREEN : Color.GRAY);
        canvas.drawLine(20, 160, getWidth() - 20, 160, railPaint);
        canvas.drawText("Output: " + (output ? "HIGH" : "LOW"), 100, 150, textPaint);
    }

    private void drawIOIndicators(Canvas canvas) {
        Paint indicatorPaint = new Paint();
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setAntiAlias(true);

        // Input A indicator
        indicatorPaint.setColor(inputA ? Color.GREEN : Color.RED);
        canvas.drawCircle(30, 80, 8, indicatorPaint);

        // Input B indicator
        indicatorPaint.setColor(inputB ? Color.GREEN : Color.RED);
        canvas.drawCircle(30, 120, 8, indicatorPaint);

        // Output indicator
        indicatorPaint.setColor(output ? Color.GREEN : Color.RED);
        canvas.drawCircle(30, 160, 8, indicatorPaint);
    }

    // Public methods for interaction
    public void setGate(String gate) {
        this.currentGate = gate;
        updateCircuitLogic();
        invalidate();
    }

    public void setInputA(boolean state) {
        this.inputA = state;
        updateCircuitLogic();
        invalidate();
    }

    public void setInputB(boolean state) {
        this.inputB = state;
        updateCircuitLogic();
        invalidate();
    }

    public void setOutput(boolean state) {
        this.output = state;
        updateCircuitLogic();
        invalidate();
    }

    public void placeIC(int x, int y, String gateType) {
        int gridX = snapToGrid(x);
        int gridY = snapToGrid(y);

        IC newIC = new IC(gridX, gridY, gateType);
        placedICs.add(newIC);
        updateCircuitLogic();
        invalidate();
    }

    public void addWire(int x, int y) {
        int gridX = snapToGrid(x);
        int gridY = snapToGrid(y);

        if (wires.size() % 2 == 0) {
            // Start new wire
            wires.add(new Wire(gridX, gridY, gridX, gridY, false));
        } else {
            // Complete the wire
            Wire lastWire = wires.get(wires.size() - 1);
            lastWire.endX = gridX;
            lastWire.endY = gridY;
            createConnections(lastWire);
        }
        updateCircuitLogic();
        invalidate();
    }

    public void placeLED(int x, int y) {
        int gridX = snapToGrid(x);
        int gridY = snapToGrid(y);

        LED newLED = new LED(gridX, gridY, false);
        leds.add(newLED);
        updateCircuitLogic();
        invalidate();
    }

    private void createConnections(Wire wire) {
        // Find components near wire endpoints and create logical connections
        for (IC ic : placedICs) {
            // Check connection to IC input A
            if (isNear(wire.startX, wire.startY, ic.x - 40, ic.y - 10) ||
                    isNear(wire.endX, wire.endY, ic.x - 40, ic.y - 10)) {
                connections.add(new Connection("INPUT_A", ic, null));
            }

            // Check connection to IC input B
            if (!ic.gateType.equals("NOT") &&
                    (isNear(wire.startX, wire.startY, ic.x - 40, ic.y + 10) ||
                            isNear(wire.endX, wire.endY, ic.x - 40, ic.y + 10))) {
                connections.add(new Connection("INPUT_B", ic, null));
            }

            // Check connection to IC output
            if (isNear(wire.startX, wire.startY, ic.x + 40, ic.y) ||
                    isNear(wire.endX, wire.endY, ic.x + 40, ic.y)) {
                connections.add(new Connection("OUTPUT", ic, null));
            }
        }

        // Check connections to LEDs
        for (LED led : leds) {
            if (isNear(wire.startX, wire.startY, led.x - 15, led.y) ||
                    isNear(wire.startX, wire.startY, led.x + 15, led.y) ||
                    isNear(wire.endX, wire.endY, led.x - 15, led.y) ||
                    isNear(wire.endX, wire.endY, led.x + 15, led.y)) {
                connections.add(new Connection("LED_INPUT", null, led));
            }
        }
    }

    private boolean isNear(int x1, int y1, int x2, int y2) {
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return distance <= CONNECTION_THRESHOLD;
    }

    private void updateCircuitLogic() {
        // Update IC states based on global inputs and connections
        for (IC ic : placedICs) {
            // Set IC inputs based on global inputs and connections
            ic.inputAState = inputA;
            ic.inputBState = inputB;

            // Calculate IC output based on gate type
            switch (ic.gateType) {
                case "AND":
                    ic.outputState = ic.inputAState && ic.inputBState;
                    break;
                case "OR":
                    ic.outputState = ic.inputAState || ic.inputBState;
                    break;
                case "NOT":
                    ic.outputState = !ic.inputAState;
                    break;
                case "NAND":
                    ic.outputState = !(ic.inputAState && ic.inputBState);
                    break;
                case "NOR":
                    ic.outputState = !(ic.inputAState || ic.inputBState);
                    break;
                case "XOR":
                    ic.outputState = ic.inputAState != ic.inputBState;
                    break;
            }

            ic.isActive = ic.inputAState || ic.inputBState || ic.outputState;
        }

        // Update wire states based on connections
        for (Wire wire : wires) {
            wire.isActive = false;
            // Check if wire is connected to active IC outputs
            for (IC ic : placedICs) {
                if (ic.outputState &&
                        (isNear(wire.startX, wire.startY, ic.x + 40, ic.y) ||
                                isNear(wire.endX, wire.endY, ic.x + 40, ic.y))) {
                    wire.isActive = true;
                    break;
                }
            }

            // Also activate if connected to input rails
            if (isNear(wire.startY, 0, 80, 0) || isNear(wire.endY, 0, 80, 0)) {
                wire.isActive = inputA;
            }
            if (isNear(wire.startY, 0, 120, 0) || isNear(wire.endY, 0, 120, 0)) {
                wire.isActive = inputB;
            }
        }

        // Update LED states based on connected IC outputs or global output
        for (LED led : leds) {
            led.isOn = false;
            led.inputState = false;

            // Check if LED is connected to any active IC output via wires
            for (IC ic : placedICs) {
                if (ic.outputState) {
                    for (Wire wire : wires) {
                        // If wire connects IC output to LED
                        if ((isNear(wire.startX, wire.startY, ic.x + 40, ic.y) ||
                                isNear(wire.endX, wire.endY, ic.x + 40, ic.y)) &&
                                (isNear(wire.startX, wire.startY, led.x - 15, led.y) ||
                                        isNear(wire.startX, wire.startY, led.x + 15, led.y) ||
                                        isNear(wire.endX, wire.endY, led.x - 15, led.y) ||
                                        isNear(wire.endX, wire.endY, led.x + 15, led.y))) {
                            led.isOn = true;
                            led.inputState = true;
                            break;
                        }
                    }
                }
                if (led.isOn) break;
            }

            // If no IC connection found, use global output
            if (!led.isOn) {
                led.isOn = output;
                led.inputState = output;
            }
        }

        // Update global output based on IC outputs
        boolean hasActiveOutput = false;
        for (IC ic : placedICs) {
            if (ic.outputState) {
                hasActiveOutput = true;
                break;
            }
        }

        if (hasActiveOutput) {
            // Find the primary IC output (last placed or most connected)
            for (IC ic : placedICs) {
                if (ic.outputState) {
                    output = ic.outputState;
                    break;
                }
            }
        }
    }

    public void reset() {
        placedICs.clear();
        wires.clear();
        leds.clear();
        connections.clear();

        currentGate = "AND";
        inputA = false;
        inputB = false;
        output = false;

        invalidate();
    }

    private int snapToGrid(int coordinate) {
        int offset = 50;
        return offset + Math.round((coordinate - offset) / (float) HOLE_SPACING) * HOLE_SPACING;
    }

    // Helper classes for components
    private static class IC {
        int x, y;
        String gateType;
        boolean inputAState = false;
        boolean inputBState = false;
        boolean outputState = false;
        boolean isActive = false;

        IC(int x, int y, String gateType) {
            this.x = x;
            this.y = y;
            this.gateType = gateType;
        }
    }

    private static class Wire {
        int startX, startY, endX, endY;
        boolean isActive;

        Wire(int startX, int startY, int endX, int endY, boolean isActive) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.isActive = isActive;
        }
    }

    private static class LED {
        int x, y;
        boolean isOn;
        boolean inputState = false;

        LED(int x, int y, boolean isOn) {
            this.x = x;
            this.y = y;
            this.isOn = isOn;
        }
    }

    private static class Connection {
        String type; // "INPUT_A", "INPUT_B", "OUTPUT", "LED_INPUT"
        IC connectedIC;
        LED connectedLED;

        Connection(String type, IC ic, LED led) {
            this.type = type;
            this.connectedIC = ic;
            this.connectedLED = led;
        }
    }
}
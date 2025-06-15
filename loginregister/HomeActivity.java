package com.example.loginregister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    private Button btnStart, btnStart1;
    private ScrollView scrollViewContent;
    private LinearLayout contentContainer;
    private LinearLayout gateExplanationsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners();
        createLogicGateExplanations();
    }

    private void initializeViews() {
        btnStart = findViewById(R.id.btnStart);
        btnStart1 = findViewById(R.id.btnStart1);
        scrollViewContent = findViewById(R.id.scrollViewContent);
        contentContainer = findViewById(R.id.contentContainer);
        gateExplanationsContainer = findViewById(R.id.gateExplanationsContainer);
    }

    private void setupClickListeners() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContent();
                Toast.makeText(HomeActivity.this, "Exploring Logic Gates on Breadboard! 🎓", Toast.LENGTH_SHORT).show();
            }
        });

        btnStart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), Breadboard.class);
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this, "Opening Breadboard Simulator! 🔌", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "Error opening Breadboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void showContent() {
        scrollViewContent.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.GONE);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        scrollViewContent.startAnimation(fadeIn);
    }

    private void hideContent() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                scrollViewContent.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);

                AlphaAnimation fadeInStart = new AlphaAnimation(0.0f, 1.0f);
                fadeInStart.setDuration(300);
                btnStart.startAnimation(fadeInStart);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        scrollViewContent.startAnimation(fadeOut);
    }

    private void createLogicGateExplanations() {
        gateExplanationsContainer.removeAllViews();

        createGateExplanation("AND Gate", "7408", createAndGateSymbol(),
                "F = A • B or F = AB",
                "Output is HIGH only when both inputs are HIGH",
                new String[]{"A", "B", "F"},
                new String[][]{{"0", "0", "0"}, {"0", "1", "0"}, {"1", "0", "0"}, {"1", "1", "1"}});

        createGateExplanation("OR Gate", "7432", createOrGateSymbol(),
                "F = A + B",
                "Output is HIGH when at least one input is HIGH",
                new String[]{"A", "B", "F"},
                new String[][]{{"0", "0", "0"}, {"0", "1", "1"}, {"1", "0", "1"}, {"1", "1", "1"}});

        createGateExplanation("NOT Gate", "7404", createNotGateSymbol(),
                "F = Ā or F = A'",
                "Output is the complement of input",
                new String[]{"A", "F"},
                new String[][]{{"0", "1"}, {"1", "0"}});

        createGateExplanation("NAND Gate", "7400", createNandGateSymbol(),
                "F = A̅B̅",
                "Output is LOW only when both inputs are HIGH",
                new String[]{"A", "B", "F"},
                new String[][]{{"0", "0", "1"}, {"0", "1", "1"}, {"1", "0", "1"}, {"1", "1", "0"}});

        createGateExplanation("NOR Gate", "7402", createNorGateSymbol(),
                "F = A̅ + B̅",
                "Output is HIGH only when both inputs are LOW",
                new String[]{"A", "B", "F"},
                new String[][]{{"0", "0", "1"}, {"0", "1", "0"}, {"1", "0", "0"}, {"1", "1", "0"}});

        createGateExplanation("XOR Gate", "7486", createXorGateSymbol(),
                "F = A ⊕ B",
                "Output is HIGH when inputs are different",
                new String[]{"A", "B", "F"},
                new String[][]{{"0", "0", "0"}, {"0", "1", "1"}, {"1", "0", "1"}, {"1", "1", "0"}});
    }

    private void createGateExplanation(String gateName, String icNumber, Bitmap gateSymbol,
                                       String algebraicFunction, String description,
                                       String[] headers, String[][] truthTableData) {

        LinearLayout gateContainer = new LinearLayout(this);
        gateContainer.setOrientation(LinearLayout.VERTICAL);
        gateContainer.setPadding(20, 20, 20, 20);
        gateContainer.setBackgroundColor(Color.parseColor("#F5F5F5"));

        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(10, 10, 10, 10);
        gateContainer.setLayoutParams(containerParams);

        TextView titleText = new TextView(this);
        titleText.setText(gateName + " (" + icNumber + ")");
        titleText.setTextSize(20);
        titleText.setTextColor(Color.BLACK);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setPadding(0, 0, 0, 10);
        gateContainer.addView(titleText);

        LinearLayout horizontalContainer = new LinearLayout(this);
        horizontalContainer.setOrientation(LinearLayout.HORIZONTAL);
        horizontalContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout symbolContainer = new LinearLayout(this);
        symbolContainer.setOrientation(LinearLayout.VERTICAL);
        symbolContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));

        ImageView gateSymbolView = new ImageView(this);
        gateSymbolView.setImageBitmap(gateSymbol);
        gateSymbolView.setLayoutParams(new LinearLayout.LayoutParams(200, 150));
        gateSymbolView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        symbolContainer.addView(gateSymbolView);

        TextView functionText = new TextView(this);
        functionText.setText("Function: " + algebraicFunction);
        functionText.setTextSize(14);
        functionText.setTextColor(Color.BLUE);
        functionText.setTypeface(null, android.graphics.Typeface.BOLD);
        functionText.setPadding(0, 10, 0, 5);
        symbolContainer.addView(functionText);

        horizontalContainer.addView(symbolContainer);

        LinearLayout truthTableContainer = new LinearLayout(this);
        truthTableContainer.setOrientation(LinearLayout.VERTICAL);
        truthTableContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));

        TextView truthTableTitle = new TextView(this);
        truthTableTitle.setText("Truth Table");
        truthTableTitle.setTextSize(16);
        truthTableTitle.setTextColor(Color.BLACK);
        truthTableTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        truthTableTitle.setPadding(0, 0, 0, 10);
        truthTableContainer.addView(truthTableTitle);

        createTruthTable(truthTableContainer, headers, truthTableData);

        horizontalContainer.addView(truthTableContainer);
        gateContainer.addView(horizontalContainer);

        TextView descriptionText = new TextView(this);
        descriptionText.setText("Description: " + description);
        descriptionText.setTextSize(14);
        descriptionText.setTextColor(Color.DKGRAY);
        descriptionText.setPadding(0, 15, 0, 0);
        gateContainer.addView(descriptionText);

        gateExplanationsContainer.addView(gateContainer);
    }

    private void createTruthTable(LinearLayout container, String[] headers, String[][] data) {
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setBackgroundColor(Color.parseColor("#E0E0E0"));
        headerRow.setPadding(5, 5, 5, 5);

        for (String header : headers) {
            TextView headerCell = new TextView(this);
            headerCell.setText(header);
            headerCell.setTextSize(14);
            headerCell.setTextColor(Color.BLACK);
            headerCell.setTypeface(null, android.graphics.Typeface.BOLD);
            headerCell.setPadding(15, 8, 15, 8);
            headerCell.setBackgroundColor(Color.parseColor("#CCCCCC"));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            );
            params.setMargins(1, 1, 1, 1);
            headerCell.setLayoutParams(params);

            headerRow.addView(headerCell);
        }
        container.addView(headerRow);

        for (String[] row : data) {
            LinearLayout dataRow = new LinearLayout(this);
            dataRow.setOrientation(LinearLayout.HORIZONTAL);
            dataRow.setPadding(5, 5, 5, 5);

            for (String cell : row) {
                TextView dataCell = new TextView(this);
                dataCell.setText(cell);
                dataCell.setTextSize(14);
                dataCell.setTextColor(Color.BLACK);
                dataCell.setPadding(15, 8, 15, 8);
                dataCell.setBackgroundColor(Color.WHITE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
                );
                params.setMargins(1, 1, 1, 1);
                dataCell.setLayoutParams(params);

                dataRow.addView(dataCell);
            }
            container.addView(dataRow);
        }
    }

    private Bitmap createAndGateSymbol() {
        Bitmap bitmap = Bitmap.createBitmap(200, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        canvas.drawColor(Color.WHITE);
        drawAndGateSymbol(canvas, paint, 50, 30, 80, 60);

        return bitmap;
    }

    private Bitmap createOrGateSymbol() {
        Bitmap bitmap = Bitmap.createBitmap(200, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        canvas.drawColor(Color.WHITE);
        drawOrGateSymbol(canvas, paint, 50, 30, 80, 60);

        return bitmap;
    }

    private Bitmap createNotGateSymbol() {
        Bitmap bitmap = Bitmap.createBitmap(200, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        canvas.drawColor(Color.WHITE);
        drawNotGateSymbol(canvas, paint, 60, 40, 60, 40);

        return bitmap;
    }

    private Bitmap createNandGateSymbol() {
        Bitmap bitmap = Bitmap.createBitmap(200, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        canvas.drawColor(Color.WHITE);
        drawNandGateSymbol(canvas, paint, 50, 30, 80, 60);

        return bitmap;
    }

    private Bitmap createNorGateSymbol() {
        Bitmap bitmap = Bitmap.createBitmap(200, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        canvas.drawColor(Color.WHITE);
        drawNorGateSymbol(canvas, paint, 50, 30, 80, 60);

        return bitmap;
    }

    private Bitmap createXorGateSymbol() {
        Bitmap bitmap = Bitmap.createBitmap(200, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        canvas.drawColor(Color.WHITE);
        drawXorGateSymbol(canvas, paint, 50, 30, 80, 60);

        return bitmap;
    }

    private void drawAndGateSymbol(Canvas canvas, Paint paint, float x, float y, float width, float height) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + width * 0.6f, y);
        path.quadTo(x + width, y, x + width, y + height / 2);
        path.quadTo(x + width, y + height, x + width * 0.6f, y + height);
        path.lineTo(x, y + height);
        path.close();
        canvas.drawPath(path, paint);

        canvas.drawLine(x - 20, y + height * 0.3f, x, y + height * 0.3f, paint);
        canvas.drawLine(x - 20, y + height * 0.7f, x, y + height * 0.7f, paint);
        canvas.drawLine(x + width, y + height / 2, x + width + 20, y + height / 2, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(16);
        canvas.drawText("A", x - 35, y + height * 0.3f + 5, textPaint);
        canvas.drawText("B", x - 35, y + height * 0.7f + 5, textPaint);
        canvas.drawText("F", x + width + 25, y + height / 2 + 5, textPaint);
    }

    private void drawOrGateSymbol(Canvas canvas, Paint paint, float x, float y, float width, float height) {
        Path path = new Path();
        path.moveTo(x, y);
        path.quadTo(x + width * 0.3f, y, x + width * 0.7f, y + height * 0.2f);
        path.quadTo(x + width, y + height * 0.4f, x + width, y + height / 2);
        path.quadTo(x + width, y + height * 0.6f, x + width * 0.7f, y + height * 0.8f);
        path.quadTo(x + width * 0.3f, y + height, x, y + height);
        path.quadTo(x + width * 0.2f, y + height / 2, x, y);
        canvas.drawPath(path, paint);

        canvas.drawLine(x - 20, y + height * 0.3f, x + 8, y + height * 0.3f, paint);
        canvas.drawLine(x - 20, y + height * 0.7f, x + 8, y + height * 0.7f, paint);
        canvas.drawLine(x + width, y + height / 2, x + width + 20, y + height / 2, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(16);
        canvas.drawText("A", x - 35, y + height * 0.3f + 5, textPaint);
        canvas.drawText("B", x - 35, y + height * 0.7f + 5, textPaint);
        canvas.drawText("F", x + width + 25, y + height / 2 + 5, textPaint);
    }

    private void drawNotGateSymbol(Canvas canvas, Paint paint, float x, float y, float width, float height) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x, y + height);
        path.lineTo(x + width, y + height / 2);
        path.close();
        canvas.drawPath(path, paint);

        canvas.drawCircle(x + width + 5, y + height / 2, 5, paint);
        canvas.drawLine(x - 20, y + height / 2, x, y + height / 2, paint);
        canvas.drawLine(x + width + 10, y + height / 2, x + width + 30, y + height / 2, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(16);
        canvas.drawText("A", x - 35, y + height / 2 + 5, textPaint);
        canvas.drawText("F", x + width + 35, y + height / 2 + 5, textPaint);
    }

    private void drawNandGateSymbol(Canvas canvas, Paint paint, float x, float y, float width, float height) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + width * 0.6f, y);
        path.quadTo(x + width, y, x + width, y + height / 2);
        path.quadTo(x + width, y + height, x + width * 0.6f, y + height);
        path.lineTo(x, y + height);
        path.close();
        canvas.drawPath(path, paint);

        canvas.drawCircle(x + width + 5, y + height / 2, 5, paint);
        canvas.drawLine(x - 20, y + height * 0.3f, x, y + height * 0.3f, paint);
        canvas.drawLine(x - 20, y + height * 0.7f, x, y + height * 0.7f, paint);
        canvas.drawLine(x + width + 10, y + height / 2, x + width + 30, y + height / 2, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(16);
        canvas.drawText("A", x - 35, y + height * 0.3f + 5, textPaint);
        canvas.drawText("B", x - 35, y + height * 0.7f + 5, textPaint);
        canvas.drawText("F", x + width + 35, y + height / 2 + 5, textPaint);
    }

    private void drawNorGateSymbol(Canvas canvas, Paint paint, float x, float y, float width, float height) {
        Path path = new Path();
        path.moveTo(x, y);
        path.quadTo(x + width * 0.3f, y, x + width * 0.7f, y + height * 0.2f);
        path.quadTo(x + width, y + height * 0.4f, x + width, y + height / 2);
        path.quadTo(x + width, y + height * 0.6f, x + width * 0.7f, y + height * 0.8f);
        path.quadTo(x + width * 0.3f, y + height, x, y + height);
        path.quadTo(x + width * 0.2f, y + height / 2, x, y);
        canvas.drawPath(path, paint);

        canvas.drawCircle(x + width + 5, y + height / 2, 5, paint);
        canvas.drawLine(x - 20, y + height * 0.3f, x + 8, y + height * 0.3f, paint);
        canvas.drawLine(x - 20, y + height * 0.7f, x + 8, y + height * 0.7f, paint);
        canvas.drawLine(x + width + 10, y + height / 2, x + width + 30, y + height / 2, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(16);
        canvas.drawText("A", x - 35, y + height * 0.3f + 5, textPaint);
        canvas.drawText("B", x - 35, y + height * 0.7f + 5, textPaint);
        canvas.drawText("F", x + width + 35, y + height / 2 + 5, textPaint);
    }

    private void drawXorGateSymbol(Canvas canvas, Paint paint, float x, float y, float width, float height) {
        Path path = new Path();
        path.moveTo(x + 10, y);
        path.quadTo(x + width * 0.3f, y, x + width * 0.7f, y + height * 0.2f);
        path.quadTo(x + width, y + height * 0.4f, x + width, y + height / 2);
        path.quadTo(x + width, y + height * 0.6f, x + width * 0.7f, y + height * 0.8f);
        path.quadTo(x + width * 0.3f, y + height, x + 10, y + height);
        path.quadTo(x + width * 0.2f + 10, y + height / 2, x + 10, y);
        canvas.drawPath(path, paint);

        Path extraPath = new Path();
        extraPath.moveTo(x, y);
        extraPath.quadTo(x + width * 0.15f, y + height / 2, x, y + height);
        canvas.drawPath(extraPath, paint);

        canvas.drawLine(x - 20, y + height * 0.3f, x + 15, y + height * 0.3f, paint);
        canvas.drawLine(x - 20, y + height * 0.7f, x + 15, y + height * 0.7f, paint);
        canvas.drawLine(x + width, y + height / 2, x + width + 20, y + height / 2, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(16);
        canvas.drawText("A", x - 35, y + height * 0.3f + 5, textPaint);
        canvas.drawText("B", x - 35, y + height * 0.7f + 5, textPaint);
        canvas.drawText("F", x + width + 25, y + height / 2 + 5, textPaint);
    }
}
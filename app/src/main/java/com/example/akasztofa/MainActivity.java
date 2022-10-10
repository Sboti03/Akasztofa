package com.example.akasztofa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.animation.IntArrayEvaluator;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnMinus, btnPlus, btnTip;

    private TextView tvTip, tvKitalalt;

    private ImageView ivOutImg;

    private GameManager game;
    private char[] outCharacters;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init() {
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnTip = findViewById(R.id.btnTip);

        tvTip = findViewById(R.id.tvTip);
        tvKitalalt = findViewById(R.id.tvKitalalt);
        ivOutImg = findViewById(R.id.ivOutImg);

        btnMinus.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnTip.setOnClickListener(this);

        createAlertDialog();
        setToDefault();
    }

    private void createAlertDialog() {
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Szeretnél még egyet játszani?!");
        alertDialogBuilder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialogBuilder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setToDefault();
            }
        });
        alertDialogBuilder.setCancelable(false);
    }

    private void setToDefault() {
        game = new GameManager();
        ivOutImg.setImageBitmap(
                BitmapFactory.decodeResource(getResources(), getImageId(game.getCountOfFail()))
        );
        int lengthOfWord = game.getTextLength();
        outCharacters = new char[lengthOfWord];
        Arrays.fill(outCharacters, '_');
        setWordOut();
        tvTip.setText(game.getCurrentLetter().first);
        tvTip.setTextColor(getColorById(GameManager.LettersValueDef.Simple.id));

    }

    private int getColorById(int id) {
        int colorId = 0;
        if (id == GameManager.LettersValueDef.Simple.id) {
            colorId = ContextCompat.getColor(this, R.color.voros);
        } else {
            colorId = ContextCompat.getColor(this, R.color.black);
        }
        return colorId;
    }

    private int getImageId(String index) {
        String fileName = "akasztofa" + index;
        String PName = getApplicationContext().getPackageName();
        int imgId = getResources().getIdentifier(
                PName + ":/drawable/" + fileName, null, null
        );
        return imgId;
    }

    private void setWordOut() {
        String outWord = "";
        for (int i = 0; i < outCharacters.length - 1; i++) {
            outWord += outCharacters[i] + " ";
        }
        outWord += outCharacters[outCharacters.length - 1];
        tvKitalalt.setText(outWord);
        tvTip.setTextColor(getColorById(game.getCurrentLetter().second));
    }

    private void wrongTip() {
        if (game.StateOfGame == GameManager.GameState.GAME_OVER) {
            alertDialogShow(false);
        } else {
            String imageIndex = game.getCountOfFail();
            ivOutImg.setImageBitmap(
                    BitmapFactory.decodeResource(getResources(), getImageId(imageIndex))
            );
        }
    }

    private void alertDialogShow(boolean win) {
        if (win) {
            alertDialogBuilder.setTitle("Helyes megfejtés!");
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            alertDialogBuilder.setTitle("Nem sikert kitalálni!");
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void btnTipPressed() {
        List<Integer> match = game.Tip();
        if (match.size() != 0) {
            for (Integer i : match) {
                outCharacters[i] = game.getCurrentLetter().first.charAt(0);
            }
            setWordOut();
            if (game.StateOfGame == GameManager.GameState.WIN) {
                alertDialogShow(true);
            }
        } else {
            wrongTip();
            setWordOut();
        }
        gameMessageHandler();
    }

    private void btnPlusPressed() {
        Pair<String, Integer> letterInf = game.getNextLetter();
        if (letterInf.second == 0) {
            tvTip.setTextColor(ContextCompat.getColor(this, R.color.voros));
        } else {
            tvTip.setTextColor(Color.BLACK);
        }
        tvTip.setText(letterInf.first);
    }

    private void btnMinusPressed() {
        Pair<String, Integer> letterInf = game.getPreviousLetter();
        if (letterInf.second == 0) {
            tvTip.setTextColor(ContextCompat.getColor(this, R.color.voros));
        } else {
            tvTip.setTextColor(Color.BLACK);
        }
        tvTip.setText(letterInf.first);
    }

    private void gameMessageHandler() {
        GameManager.GameMessages message = game.getGameMessage();
        switch (message) {
            case USED:
                showToast("Ezt a betűt már tippelted!");
                break;
            case MATCH:
                showToast("Helyes tipp!");
                break;
            case NOT_MATCH:
                showToast("Rossz tipp!");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnMinus:
                btnMinusPressed();
                break;
            case R.id.btnPlus:
                btnPlusPressed();
                break;
            case R.id.btnTip:
                btnTipPressed();
                break;
        }
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        String randomSzo = game.getRandomSzo();
        List<String> letters = new ArrayList<>();
        List<Integer> lettersKeys = new ArrayList<>();
        GameManager.GameState gameState = game.getStateOfGame();
        GameManager.GameMessages gameMessage = game.getGameMessage();
        int indexOfImg = game.getIndexOfImg();
        int countOfFail = game.getFails();
        int matchCount = game.getMatchCount();
        {
            Map<String, Integer> letterVK = game.getLetters();
            for (int i = 0; i < letterVK.size(); i++) {
                String key = (String) letterVK.keySet().toArray()[i];
                Integer value = letterVK.get(key);
                letters.add(key);
                lettersKeys.add(value);
            }
        }
        outState.putString("randomSzo", randomSzo);
        {
            String[] tmp = new String[letters.size()];
            letters.toArray(tmp);
            outState.putStringArray("letters", tmp);
        }
        {
            int[] tmp = lettersKeys.stream().mapToInt(i -> i).toArray();
            outState.putIntArray("lettersKeys", tmp);
        }
        outState.putInt("gameState", gameState.ordinal());
        outState.putInt("gameMessage", gameMessage.ordinal());
        outState.putInt("indexOfImg", indexOfImg);
        outState.putInt("countOfFail", countOfFail);
        outState.putInt("matchCount", matchCount);
        outState.putCharArray("outCharacters", outCharacters);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        String randomSzo = savedInstanceState.getString("randomSzo");
        String[] letters = savedInstanceState.getStringArray("letters");
        int[] lettersKeys = savedInstanceState.getIntArray("lettersKeys");
        GameManager.GameState gameState;
        {
            int tmp = savedInstanceState.getInt("gameState");
            switch (tmp) {
                case 1:
                    gameState = GameManager.GameState.WIN;
                    break;
                case 2:
                    gameState = GameManager.GameState.GAME_IS_RUN;
                    break;
                default:
                    gameState = GameManager.GameState.GAME_OVER;
            }
        }
        GameManager.GameMessages gameMessage;
        {
            int tmp = savedInstanceState.getInt("gameMessage");
            switch (tmp) {
                case 0:
                    gameMessage = GameManager.GameMessages.NOTHING;
                    break;
                case 1:
                    gameMessage = GameManager.GameMessages.USED;
                    break;
                case 2:
                    gameMessage = GameManager.GameMessages.NOT_MATCH;
                    break;
                case 3:
                    gameMessage = GameManager.GameMessages.MATCH;
                    break;
                default:
                    gameMessage = GameManager.GameMessages.GAME_ERROR;
            }
        }
        int indexOfImg = savedInstanceState.getInt("indexOfImg");
        int countOfFail = savedInstanceState.getInt("countOfFail");
        int matchCount = savedInstanceState.getInt("matchCount");
        outCharacters = savedInstanceState.getCharArray("outCharacters");
        game = new GameManager(randomSzo, letters, lettersKeys, gameMessage, indexOfImg,
                countOfFail, matchCount, gameState
        );

        setWordOut();
        ivOutImg.setImageBitmap(
                BitmapFactory.decodeResource(getResources(), getImageId(game.getCountOfFail()))
        );
        tvTip.setText(game.getCurrentLetter().first);
        tvTip.setTextColor(getColorById(game.getCurrentLetter().second));
        if (game.StateOfGame == GameManager.GameState.WIN) {
            alertDialogShow(true);
        } else if (game.StateOfGame == GameManager.GameState.GAME_OVER) {
            alertDialogShow(false);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
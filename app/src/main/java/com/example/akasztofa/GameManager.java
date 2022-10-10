package com.example.akasztofa;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class GameManager {

    private static final String[] Szavak = {"alma", "eper", "körte",
            "autó", "ember", "karcsi", "aladár", "kicsi", "tart", "kerékpár"
    };
    private static final String
            AllLetters = "A;Á;B;C;D;E;É;F;G;H;I;Í;J;K;L;M;N;O;Ó;Ö;Ő;P;Q;R;S;T;U;Ú;Ü;Ű;V;W;X;Y;Z";


    private Map<String, Integer> letters;

    private int indexOfImg = 0;
    private Random rnd;
    private String randomSzo;
    private int countOfFail = 0;
    private int matchCount = 0;

    public enum GameState {
        GAME_OVER,
        WIN,
        GAME_IS_RUN
    }

    public enum LettersValueDef {
        Match(1),
        Used(2),
        Simple(0);

        int id;

        LettersValueDef(int id) {
            this.id = id;
        }
    }

    public enum GameMessages {
        NOTHING,
        USED,
        NOT_MATCH,
        MATCH,
        GAME_ERROR,
    }

    private GameMessages gameMessage;


    public GameState StateOfGame = GameState.GAME_IS_RUN;


    public GameManager() {
        rnd = new Random();
        randomSzo = szoGeneral();
        letters = new LinkedHashMap<>();
        {
            String[] tmp = AllLetters.split(";");
            for (String s : tmp) {
                Log.d("beto", s);
                letters.put(s, 0);
            }
        }
        Log.d("szo", randomSzo);
        gameMessage = GameMessages.NOTHING;
    }

    public GameManager(String randomSzo, String[] letters, int[] lettersKey,
                       GameMessages gameMessage, int indexOfImg,int countOfFail,
                       int matchCount, GameState gameState) {
        this.randomSzo = randomSzo;
        this.letters = new LinkedHashMap<>();
        {
            for (int i = 0; i < letters.length; i++) {
                this.letters.put(letters[i], lettersKey[i]);
            }
        }
        this.gameMessage = gameMessage;
        this.indexOfImg = indexOfImg;
        this.countOfFail = countOfFail;
        this.matchCount = matchCount;
        this.StateOfGame = gameState;
    }

    public String getRandomSzo() {
        return randomSzo;
    }

    public int getFails() {
        return countOfFail;
    }

    public GameState getStateOfGame() {
        return StateOfGame;
    }


    public Map<String, Integer> getLetters() {
        return letters;
    }

    public int getIndexOfImg() {
        return indexOfImg;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public GameMessages getGameMessage() {
        GameMessages tmp = gameMessage;
        gameMessage = GameMessages.NOTHING;
        return tmp;
    }

    public Pair<String, Integer> getNextLetter() {

        if ((indexOfImg + 1) >= letters.size()) {
            indexOfImg = 0;
        } else {
            indexOfImg++;
        }
        String key = (String) letters.keySet().toArray()[indexOfImg];
        Integer value = (Integer) letters.get(key);
        Log.d("kv", key + value);
        return new Pair<>(key, value);
    }

    public Pair<String, Integer> getPreviousLetter() {

        if ((indexOfImg - 1) < 0) {
            indexOfImg = letters.size() - 1;
        } else {
            indexOfImg--;
        }
        String key = (String) letters.keySet().toArray()[indexOfImg];
        Integer value = (Integer) letters.get(key);
        Log.d("kv", key + value + indexOfImg);
        return new Pair<>(key, value);
    }

    public Pair<String, Integer> getCurrentLetter() {
        String key = (String) letters.keySet().toArray()[indexOfImg];
        Integer value = (Integer) letters.get(key);
        return new Pair<>(key, value);
    }


    private String szoGeneral() {
        int rndIndex = rnd.nextInt(Szavak.length);
        return Szavak[rndIndex].toUpperCase(Locale.ROOT);
    }

    public int getTextLength() {
        return randomSzo.length();
    }


    public List<Integer> Tip() {

        if (getCurrentLetter().second != LettersValueDef.Simple.id) {
            gameMessage = GameMessages.USED;
            return new ArrayList<>();
        }

        char tipped = getCurrentLetter().first.charAt(0);
        List<Integer> match = new ArrayList<>();
        for (int i = 0; i < randomSzo.length(); i++) {
            if (randomSzo.charAt(i) == tipped) {
                match.add(i);
            }
        }

        if (match.size() == 0) {
            letters.put(getCurrentLetter().first, LettersValueDef.Used.id);
            countOfFail++;
            if (countOfFail == 13) {
                StateOfGame = GameState.GAME_OVER;
            }
            gameMessage = GameMessages.NOT_MATCH;
        } else {
            letters.put(getCurrentLetter().first, LettersValueDef.Match.id);
            matchCount += match.size();
            if (matchCount == randomSzo.toCharArray().length) {
                StateOfGame = GameState.WIN;
            }
            gameMessage = GameMessages.MATCH;
        }
        return match;
    }

    public String getCountOfFail() {
        String formatedIndex = String.valueOf(countOfFail);
        if (countOfFail < 10) {
            formatedIndex = "0" + countOfFail;
        }
        Log.d("img", formatedIndex);
        return formatedIndex;
    }

}

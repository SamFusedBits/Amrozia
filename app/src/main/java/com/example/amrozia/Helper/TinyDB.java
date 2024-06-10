import android.content.Context;
import android.content.SharedPreferences;

import com.example.amrozia.Domain.ItemsDomain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TinyDB {

    private SharedPreferences preferences;
    private String DEFAULT_APP_PREF = "tinydb";
    private Gson gson;

    public TinyDB(Context context) {
        preferences = context.getSharedPreferences(DEFAULT_APP_PREF, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void putListObject(String key, ArrayList<ItemsDomain> list) {
        String json = gson.toJson(list);
        preferences.edit().putString(key, json).apply();
    }

    public ArrayList<ItemsDomain> getListObject(String key) {
        String json = preferences.getString(key, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<ItemsDomain>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

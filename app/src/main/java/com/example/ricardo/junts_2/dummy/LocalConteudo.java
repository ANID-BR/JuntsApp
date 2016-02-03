package com.example.ricardo.junts_2.dummy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class LocalConteudo {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<LocalItem> ITEMS = new ArrayList<LocalItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, LocalItem> ITEM_MAP = new HashMap<String, LocalItem>();

    public LocalConteudo ( String jsonLocais ) throws JSONException {

        JSONArray jsonArray = new JSONArray(jsonLocais);

        ITEMS.clear();
        ITEM_MAP.clear();

        int tamanhoJsonArray = jsonArray.length();
        for (int i = 0; i < tamanhoJsonArray; i++) {
            JSONObject itemJsonArray = jsonArray.getJSONObject(i);
            String id = itemJsonArray.getString("id");
            String nome = itemJsonArray.getString("nome");
            String localizacao = itemJsonArray.getString("localizacao");
            Double latitude = itemJsonArray.getDouble("latitude");
            Double longitude = itemJsonArray.getDouble("longitude");
            addItem(new LocalItem(String.valueOf(i+1),
                                  id,
                                  nome,
                                  localizacao,
                                  latitude,
                                  longitude));
        }

    }

    private static void addItem(LocalItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class LocalItem {
        public final String seq;
        public final String id;
        public final String nome;
        public final String detalhes;
        public final Double latitude;
        public final Double longitude;

        public LocalItem(String seq, String id, String nome, String detalhes, Double latitude, Double longitude) {
            this.seq = seq;
            this.id = id;
            this.nome = nome;
            this.detalhes = detalhes;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return nome;
        }
    }
}

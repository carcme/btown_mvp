package me.carc.btown.data.reverse;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReverseServiceProvider {

    private static ReverseApi service;

    public static ReverseApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static ReverseApi createService() {
/*
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AddressReverse.class, new RouteTypeAdapter())
                .create();
*/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ReverseApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ReverseApi.class);
    }

/*
    public static class RouteTypeAdapter implements JsonDeserializer<AddressReverse> {
        @Override
        public AddressReverse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Gson gson = new Gson();
            AddressReverse routeList = new AddressReverse();
            JsonObject jsonObject = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> elementJson : jsonObject.entrySet()) {
                AddressReverse wardsRoutes = gson.fromJson(elementJson.getValue().getAsJsonArray(), AddressReverse.class);

            }
            return routeList;
        }
    }
*/
}
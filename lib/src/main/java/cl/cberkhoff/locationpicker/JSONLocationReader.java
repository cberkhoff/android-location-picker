package cl.cberkhoff.locationpicker;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads locations from a JSON file.
 *
 * An Array of locations, each being an array, is expected. Any of the following formats is
 * supported (resulting value at the side).
 *
 * ["name"]                     => new Location(null, null, "name")
 * [id, "name"]                 => new Location(id, null, "name")
 * [id, "name", parentId]       => new Location(id, parentId, "name")
 *
 * Created by Christian on 08-11-13.
 */
public class JSONLocationReader implements LocationReader{

    final String assetsFilename;
    final Context context;

    public JSONLocationReader(Context context, String assetsFilename){
        this.assetsFilename = assetsFilename;
        this.context = context;
    }

    @Override
    public List<Location> readLocations() {
        JSONArray l = null;
        try {
            l = new JSONArray(slurp(context.getAssets().open(assetsFilename), 1024));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Location>();
        }
        ArrayList<Location> r = new ArrayList<Location>(l.length());

        for(int i=0; i < l.length(); i++){
            JSONArray v = null;
            try {
                v = l.getJSONArray(i);

                if(v.length() == 1){
                    r.add(new Location(null, null, v.getString(0)));
                } else if(v.length() == 2){
                    r.add(new Location(v.getInt(0), null, v.getString(1)));
                } else if (v.length() > 2){
                    r.add(new Location(v.getInt(0), v.getInt(2), v.getString(1)));
                }
            } catch (JSONException e) {
                continue;
            }
        }
        return r;
    }

    public static String slurp(final InputStream is, final int bufferSize)
    {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try {
            final Reader in = new InputStreamReader(is, "UTF-8");
            try {
                for (;;) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                        break;
                    out.append(buffer, 0, rsz);
                }
            }
            finally {
                in.close();
            }
        }
        catch (Exception ex) {
            return "";
        }
        return out.toString();
    }
}

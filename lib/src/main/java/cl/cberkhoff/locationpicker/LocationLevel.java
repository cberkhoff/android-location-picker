package cl.cberkhoff.locationpicker;

import android.content.Context;
import android.widget.EditText;

/**
 * Created by Christian on 08-11-13.
 */
public class LocationLevel {

    private LocationLevel child;
    private LocationAdapter adapter;
    private int promptResourceId;
    private EditText editText;

    public LocationLevel(LocationLevel child, LocationAdapter adapter, int promptResourceId) {
        this.child = child;
        this.adapter = adapter;
        this.promptResourceId = promptResourceId;
    }

    public LocationLevel getChild() {
        return child;
    }

    public LocationAdapter getAdapter() {
        return adapter;
    }

    public int getPromptResourceId() {
        return promptResourceId;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public static void chileanLocationLevels(final Context c, final LocationPicker lp){
        final LocationLevel comuna = new LocationLevel(
                null,
                new LocationAdapter(c, new JSONLocationReader("cl_comunas.json")),
                R.string.comuna);
    }
}

package cl.cberkhoff.locationpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

/**
 * Created by CBerkhoff on 08-10-13.
 */
public class LocationPicker extends LinearLayout{

    private LocationLevel rootLocationLevel;

    public LocationPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * Creates a Location selector for the given level, and makes a recursive call if a child is
     * present.
     *
     * @param ll
     */
    public void setLevels(final LocationLevel ll){
        this.rootLocationLevel = ll;
        setLevelsWithoutRoot(ll);
    }

    private void setLevelsWithoutRoot(final LocationLevel ll){
        final EditText e = new EditText(getContext());
        ll.setEditText(e);
        addView(e);

        e.setKeyListener(null);
        e.setHint(ll.getPromptResourceId());

        configureSpinnerBehaviour(
                e,
                getContext().getString(ll.getPromptResourceId()),
                ll.getAdapter(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        // store values
                        setupEditText(ll.getAdapter().getItem(position), ll);
                        dialog.dismiss();
                    }
                });

        if(ll.getChild() != null){
            setLevelsWithoutRoot(ll.getChild());
        }

        // initially all childs must be hidden
        hideChilds(ll);
    }

    private void setupEditText(final Location selectedLocation, final LocationLevel ll){
        ll.getEditText().setText(selectedLocation.getName());
        ll.getEditText().setVisibility(View.VISIBLE);

        // configure child
        if(ll.childHasParent(selectedLocation)){
            final LocationLevel c = ll.getChild();
            c.getAdapter().getFilter().filter((CharSequence) (selectedLocation.getId()+""));

            c.getEditText().setVisibility(View.VISIBLE);
            c.getEditText().setText(null);

            // hide grandchilds
            hideChilds(c);
        } else {
            if(ll.getChild() != null){
                hideChilds(ll);
            }
        }
    }

    /**
     * Recursively hides (View.GONE) childs
     *
     * @param ll
     */
    public void hideChilds(final LocationLevel ll){
        final LocationLevel c = ll.getChild();
        if(c!=null){
            c.getEditText().setVisibility(View.GONE);
            hideChilds(c);
        }
    }

    private void configureSpinnerBehaviour(final EditText et, final String prompt, final ListAdapter adapter, final DialogInterface.OnClickListener ocl){
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(prompt)
                .setAdapter(adapter, ocl)
                .create();

        et.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    dialog.show();
                }
            }
        });
        et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    public void setError(String error){
        rootLocationLevel.getEditText().setError(error);
    }

    public String getText(){
        return getText(rootLocationLevel);
    }

    public boolean allSet(){
        return allSet(rootLocationLevel);
    }

    private boolean allSet(LocationLevel ll){
        if(ll.getEditText().getVisibility() == View.VISIBLE){
            if(TextUtils.isEmpty(ll.getEditText().getText())){
                return false;
            } else {
                if(ll.getChild() != null){
                    return allSet(ll.getChild());
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
    }

    private String getText(LocationLevel ll){
        final String child = ll.getChild() == null ? "" : getText(ll.getChild());

        if(ll.getEditText().getVisibility() == View.VISIBLE){
            if(TextUtils.isEmpty(ll.getEditText().getText())){
                return "";
            } else {
                return (TextUtils.isEmpty(child) ? "" : child + ", ") + ll.getEditText().getText().toString();
            }
        } else {
            return "";
        }
    }

    public void setText(String text){
        if(text != null){
            setText(rootLocationLevel, text);
        }
    }

    private void setText(LocationLevel ll, String text){
        final Pair<String, String> firstRest = lastSubstringSplit(text);

        final Location location = ll.getAdapter().getLocationByName(firstRest.first);
        if(location != null){
            setupEditText(location, ll);
        } else {
            return;
        }

        if(ll.getChild() != null && firstRest.second != null){
            setText(ll.getChild(), firstRest.second);
        }
    }

    private Pair<String, String> lastSubstringSplit(String text){
        final int commaIndex = text.lastIndexOf(",");
        if(commaIndex == -1){
            return new Pair<String, String>(text.trim(), null);
        } else {
            return new Pair<String, String>(text.substring(commaIndex+1).trim(), text.substring(0, commaIndex).trim());
        }
    }

}

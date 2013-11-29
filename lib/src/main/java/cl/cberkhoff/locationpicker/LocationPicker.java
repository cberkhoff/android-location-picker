package cl.cberkhoff.locationpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

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
                        final Location selectedLocation= ll.getAdapter().getItem(position);
                        e.setText(selectedLocation.getName());

                        // configure child
                        if(ll.isChildValid(selectedLocation)){
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

                        dialog.dismiss();
                    }
                });

        if(ll.getChild() != null){
            setLevels(ll.getChild());
        }

        // initially all childs must be hidden
        hideChilds(ll);
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

}

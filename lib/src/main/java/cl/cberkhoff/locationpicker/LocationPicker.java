package cl.acid.lucas.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.util.List;

import cl.acid.lucas.R;
import cl.acid.lucas.activities.LucasActivity;
import cl.acid.lucas.adapters.NameableArrayAdapter;
import cl.acid.lucas.models.Commune;
import cl.acid.lucas.models.CommuneDao;
import cl.acid.lucas.models.DaoSession;
import cl.acid.lucas.models.Region;
import cl.acid.lucas.models.RegionDao;

/**
 * Created by CBerkhoff on 08-10-13.
 */
public class RegionCommuneSelector extends LinearLayout{

    private EditText regionSpinner, communeSpinner;
    private String regionPrompt, communePrompt;
    private Region selectedRegion;
    private Commune selectedComunne;

    private DaoSession getDaoSession(){
        return ((LucasActivity) getContext()).getDaoSession();
    }

    public EditText getRegionSpinner() {
        return regionSpinner;
    }

    public EditText getCommuneSpinner() {
        return communeSpinner;
    }

    public RegionCommuneSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RegionCommuneSelector, 0, 0);
        try {
            regionPrompt = a.getString(R.styleable.RegionCommuneSelector_regionPrompt);
            communePrompt = a.getString(R.styleable.RegionCommuneSelector_communePrompt);
        } finally {
            a.recycle();
        }

        setOrientation(LinearLayout.VERTICAL);

        final LayoutInflater i = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        i.inflate(R.layout.view_region_commune_selector, this, true);

        regionSpinner = (EditText) findViewById(R.id.region);
        communeSpinner = (EditText) findViewById(R.id.commune);

        regionSpinner.setKeyListener(null);
        communeSpinner.setKeyListener(null);

        regionSpinner.setHint(regionPrompt);
        communeSpinner.setHint(communePrompt);

        // let's retrieve the regions from the DB
        final List<Region> regions = getDaoSession().getRegionDao().queryBuilder().orderAsc(RegionDao.Properties.Order).list();

        configureSpinnerBehaviour(
                regionSpinner,
                regionPrompt,
                new NameableArrayAdapter<Region>(getContext(), regions),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                selectedRegion= regions.get(position);
                regionSpinner.setText(selectedRegion.getName());
                final List<Commune> communes = getDaoSession().getCommuneDao().queryBuilder()
                        .orderAsc(CommuneDao.Properties.Name)
                        .where(CommuneDao.Properties.RegionId.eq(regions.get(position).getId()))
                        .list();

                communeSpinner.setVisibility(View.VISIBLE);
                communeSpinner.setText(null);

                dialog.dismiss();

                configureSpinnerBehaviour(
                        communeSpinner,
                        communePrompt,
                        new NameableArrayAdapter<Commune>(getContext(), communes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                selectedComunne = communes.get(position);
                                communeSpinner.setText(selectedComunne.getName());
                                dialog.dismiss();
                            }
                        });


            }
        });


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

    public Commune getSelectedCommune(){
        return selectedComunne;
    }
    public Region getSelectedRegion(){
        return selectedRegion;
    }

}

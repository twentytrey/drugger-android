package xyz.drugger.app.section;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SingleChoiceDialogFragment extends DialogFragment {
    Context context;
    String [] namelist;
    int position=0;

    public interface  SingleChoiceListener {
        void onPositiveButtonClicked(String[] list, int position);
        void onNegativeButtonClicked();
    }

    SingleChoiceListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener=(SingleChoiceListener)context;
        }
        catch(Exception e){
            throw new ClassCastException(getActivity().toString()+" SingleChoiceListener must be implemented");
        }
    }

    public SingleChoiceDialogFragment(Context context, ArrayList<String> list){
        this.context=context;
        namelist=list.toArray(new String[0]);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        String[] list=namelist;
        builder.setTitle("Select a store")
                .setSingleChoiceItems(list, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        position=which;
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onPositiveButtonClicked(namelist,position);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onNegativeButtonClicked();
            }
        });
        return builder.create();
    }
}
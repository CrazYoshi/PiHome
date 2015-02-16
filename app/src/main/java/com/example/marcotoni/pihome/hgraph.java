package com.example.marcotoni.pihome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONArray;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link hgraph.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link hgraph#newInstance} factory method to
 * create an instance of this fragment.
 */
public class hgraph extends Fragment {

    private OnFragmentInteractionListener mListener;
    WebView graphview;
    JSONArray arrayList;

    public static hgraph newInstance() {
        hgraph fragment = new hgraph();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public hgraph() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View hgraphview = inflater.inflate(R.layout.fragment_hgraph, null);
        graphview = (WebView) hgraphview.findViewById(R.id.HumwebView);

        graphview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        graphview.getSettings().setAllowFileAccessFromFileURLs(true);
        graphview.getSettings().setJavaScriptEnabled(true);
        graphview.addJavascriptInterface(this, "android");
        graphview.setOnTouchListener(new View.OnTouchListener(){ //Disable touch events
            @Override
            public boolean onTouch(View v, MotionEvent event){
                return true;
            }
        });
        graphview.loadUrl("file:///android_asset/hgraph.html");

        new graphDataRead().execute();
        // Inflate the layout for this fragment
        return hgraphview;
    }

    /** This passes our data out to the JS */
    @JavascriptInterface
    public String getData() {
        return arrayList.toString();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class graphDataRead extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            if(isAdded()) {
                RPiClient clt = new RPiClient(getActivity().getApplicationContext());
                return clt.sendRequest("selectDHTinfo");
            }
            else return null;
        }

        @Override
        protected void onPostExecute(Object result){
            super.onPostExecute(result);
            arrayList = (JSONArray) result;
            graphview.reload();
        }
    }
}

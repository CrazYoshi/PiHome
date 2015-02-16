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
import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link dashboard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dashboard extends Fragment {

    private OnFragmentInteractionListener mListener;
    WebView dashview;
    JSONArray arrayList;

    public static dashboard newInstance() {
        dashboard fragment = new dashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public dashboard() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList = new JSONArray();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dview = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dashview = (WebView) dview.findViewById(R.id.DashWebView);

        dashview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        dashview.getSettings().setAllowFileAccessFromFileURLs(true);
        dashview.getSettings().setJavaScriptEnabled(true);
        dashview.addJavascriptInterface(this, "android");
        dashview.setOnTouchListener(new View.OnTouchListener(){ //Disable touch events
            @Override
            public boolean onTouch(View v, MotionEvent event){
                return true;
            }
        });
        dashview.loadUrl("file:///android_asset/dashboard.html");
        new dhtDataRead().execute();

        // Inflate the layout for this fragment
        return dview;
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

    private class dhtDataRead extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            if(isAdded()) {
                RPiClient clt = new RPiClient(getActivity().getApplicationContext());
                return clt.sendRequest("dhtRead");
            }
            else return null;
        }

        @Override
        protected void onPostExecute(Object result){
            super.onPostExecute(result);
            try {
                if (result!=null) {
                    arrayList.put(Math.round((((JSONArray) result).getDouble(0)) * 100.0) / 100.0);
                    arrayList.put(Math.round((((JSONArray) result).getDouble(1)) * 100.0) / 100.0);
                }
            }
            catch (JSONException e) { e.printStackTrace(); }
            dashview.reload();
        }
    }
}

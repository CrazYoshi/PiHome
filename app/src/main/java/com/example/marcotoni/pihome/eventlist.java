package com.example.marcotoni.pihome;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link eventlist.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link eventlist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class eventlist extends Fragment {
    private OnFragmentInteractionListener mListener;
    private ListView EventListView;
    private CustomAdapter eventAdapter;

    public static eventlist newInstance() {
        eventlist fragment = new eventlist();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public eventlist() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View eventView = inflater.inflate(R.layout.fragment_eventlist, null);
        EventListView = (ListView) eventView.findViewById(R.id.EventListView);
        /*EventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });*/

        eventAdapter = new CustomAdapter(eventView.getContext(), new ActionBarCallBack());
        EventListView.setAdapter(eventAdapter);

        new EventDataRead().execute();
        return eventView;   // Inflate the layout for this fragment
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                deleteEventList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.eventlist_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_delete:
                deleteEventList();
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }*/

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

    private void fillEventList(JSONArray array){
        DateTime dateTime = DateTime.now();
        try {
            eventAdapter.deleteItems(); //clean ArrayLists
            for(int i=0; i<array.length(); i++){
                JSONArray jsonArray = array.getJSONArray(i);
                DateTime jsonDate = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSS").parseDateTime(jsonArray.getString(3));

                if (DateTimeComparator.getDateOnlyInstance().compare(dateTime, jsonDate) != 0 || i == 0){
                    dateTime = jsonDate;
                    EventListItem itemHeader = new EventListItem();

                    if (DateTimeComparator.getDateOnlyInstance().compare(jsonDate, DateTime.now()) == 0) { itemHeader.setTitle("Oggi"); eventAdapter.addSectionHeaderItem(itemHeader); }
                    else if (DateTimeComparator.getDateOnlyInstance().compare(jsonDate, DateTime.now().minusDays(1)) == 0) { itemHeader.setTitle("Ieri"); eventAdapter.addSectionHeaderItem(itemHeader); }
                    else { itemHeader.setTitle(jsonDate.toString("EEEE dd")); eventAdapter.addSectionHeaderItem(itemHeader); }
                }
                EventListItem item = new EventListItem();
                item.setId(jsonArray.getInt(0));
                item.setTitle(jsonArray.getString(2));
                item.setType(jsonArray.getString(1));
                item.setDescription(jsonDate.toString("HH:mm"));
                eventAdapter.addItem(item);
            }
            eventAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteEventList(){
        JSONArray array = new JSONArray();
        for(int i=0;i<eventAdapter.getCount();i++){
            if(eventAdapter.getItem(i).isChecked()) array.put(eventAdapter.getItem(i).getID());
        }
        new EventDataDelete().execute(array);
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

    private class EventDataRead extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            String address = "http://localhost:8080";
            if(isAdded()) {
                address = getString(R.string.JSONRPCserver);
            }
            RPiClient clt = new RPiClient(address);
            return clt.sendRequest("selectEvents");
        }

        @Override
        protected void onPostExecute(Object result){
            super.onPostExecute(result);
            if(result != null){ fillEventList((JSONArray) result); }
        }
    }

    private class EventDataDelete extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            String address = "http://localhost:8080";
            if(isAdded()) {
                address = getString(R.string.JSONRPCserver);
            }
            RPiClient clt = new RPiClient(address);
            return clt.sendRequest("deleteEvent",objects);
        }

        @Override
        protected void onPostExecute(Object result){
            super.onPostExecute(result);
            if(result != null){ fillEventList((JSONArray) result); }
        }
    }

    public class ActionBarCallBack implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.eventlist_actions,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_delete:
                    deleteEventList();
                    eventAdapter.PostDeleteAction();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    }
}

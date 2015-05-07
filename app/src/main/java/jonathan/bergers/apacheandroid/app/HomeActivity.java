package jonathan.bergers.apacheandroid.app;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import applib.ROClient;
import applib.exceptions.ConnectionException;
import applib.exceptions.InvalidCredentialException;
import applib.exceptions.UnknownErrorException;
import applib.representation.Homepage;



/* Author - Dimuthu Upeksha*/




public class HomeActivity extends Activity{

    TextView textView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        textView = (TextView)findViewById(R.id.textView);

        
        new HomeTask(HomeActivity.this).execute();

    }
    
    private class HomeTask extends AsyncTask<Void, Void, Homepage> {
        private final ProgressDialog pd;
        private final HomeActivity activity;
        int error = 0;
        private static final int INVALID_CREDENTIAL = -1;
        private static final int CONNECTION_ERROR = -2;
        private static final int UNKNOWN_ERROR = -3;

        private HomeTask(HomeActivity activity) {
            pd = new ProgressDialog(activity);
            pd.setMessage("Loading HomePage");
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Homepage doInBackground(Void... voids) {
            Homepage homePage = Model.getInstance().getHomePage();

            if (homePage == null) {
                try {
                    homePage = ROClient.getInstance().homePage();
                } catch (ConnectionException e) {
                    error = CONNECTION_ERROR;
                    e.printStackTrace();
                } catch (InvalidCredentialException e) {
                    error = INVALID_CREDENTIAL;
                    e.printStackTrace();
                } catch (UnknownErrorException e) {
                    error = UNKNOWN_ERROR;
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return homePage;
        }

        @Override
        protected void onPostExecute(Homepage homePage) {
            if (homePage != null) {
                Model.getInstance().setHomePage(homePage);

                //activity.render();
                textView.setText(homePage.getLinks().toString());
            }

            if (error == INVALID_CREDENTIAL) {
                /* Username and password not valid show the Login */

                textView.setText("INVALID CREDENTIAL");

             //   Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
             //   HomeActivity.this.startActivity(intent);
            }

            if (error == CONNECTION_ERROR) {
                /** Show the error Dialog */
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Connection Error");
                alertDialog.setMessage("Please check your settings.");

                // Setting OK Button
                alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }

            if(error == UNKNOWN_ERROR){
               // Intent intent = new Intent(HomeActivity.this,InitialAvtivity.class);
                Toast.makeText(getApplicationContext(), "Unknown error - Tryusing another url", Toast.LENGTH_SHORT).show();
               // startActivity(intent);
            }
            pd.hide();
        }




    }

}

package com.facturasff.sff;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    Context  context;
    public WebView webView;
    private ImageView imageConnect;
    private TextView msjCon, msjConnection;
    Timer timer;
    boolean connectNew, connectOld;
    int TIME_WAIT_CHECK;
    int TIME_CHECK;
    String url = "https://facturasff.com";
    //String url = "https://192.168.1.93:85";

    private String filename = "Factura.pdf";
    private String filenameXML = "Factura.xml";
    private String filenameExcel = "Factura.xlsx";
    int ContadorPdf=1,ContadorXML=1,ContadorExcel=1;
    private String filepath = "Documents";
    File myExternalFile;
    File myExternalFilexsl;
    File myExternalFilexml;
    String myData = "";
    String MyUrl;
    String UrlXml;
    String UrlPdf;
    String UrlXls;

    private AlertError AlertError;
    private Boolean AlertaMostrada=false;

    String base64,base64xml,base64pdf,base64xls;
    RelativeLayout SplashLayout;
    ArrayList<DownloadInfo> DownloadArray=new ArrayList<DownloadInfo>();

    public View Dialogo;
    public Dialog AlertErrordialog;
    private String Type="Error";
    public Button btnCancelar;
    public Button btnAceptar;
    ListView listViewOpcionesDescarga;



    private ValueCallback<Uri[]> filePathCallback;
    //Codigo para startActivityForResult;
    private static final int REQUEST_FILE = 5003;
    private static String TAG="TAG";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SplashLayout = findViewById(R.id.SplashLayout);
        context=this;
        webView = (WebView) findViewById(R.id.webViewUrl);
        imageConnect = (ImageView) findViewById(R.id.imgConexion);
        msjCon = (TextView) findViewById(R.id.tVMsjCon);
        msjConnection = (TextView) findViewById(R.id.tVMsjConnection);



        TIME_WAIT_CHECK = Integer.parseInt(getResources().getString(R.string.time_wait_check));
        TIME_CHECK = Integer.parseInt(getResources().getString(R.string.time_check));

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_logoice);
        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);*/

        networkConnected nc = new networkConnected();

        timer = new Timer();
        timer.scheduleAtFixedRate(nc, TIME_WAIT_CHECK, TIME_CHECK);

        if (connectNew) {
            connectOld = false;
        } else {
            connectOld = true;
        }

        if (isNetworkConnected()) {
            chargeWeb(String.valueOf(Html.fromHtml(url)));
        }

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            //saveButton.setEnabled(false);
        }else {

            //myExternalFile = new File(getExternalFilesDir(filepath), filename);
            myExternalFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), filename);
            //myExternalFilexsl = new File(getExternalFilesDir(filepath), filenameExcel);
            myExternalFilexsl = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), filenameExcel);
            //myExternalFilexml = new File(getExternalFilesDir(filepath), filenameXML);
            myExternalFilexml = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), filenameXML);
        }
        ActivityCompat.requestPermissions(MainActivity.this,  new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);


    }

    //Metodo que impide que el boton atras cierre la aplicacion
    public void onBackPressed(){

        if (webView.canGoBack()){
            webView.goBack();
        } else {
            super.finish();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isAvailable()) { // No existe conexión
            imageConnect.setVisibility(View.VISIBLE);
            msjCon.setVisibility(View.VISIBLE);
            msjConnection.setVisibility(View.VISIBLE);
            return false;
        } else { // Existe conexión
            imageConnect.setVisibility(View.INVISIBLE);
            msjCon.setVisibility(View.INVISIBLE);
            msjConnection.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    @SuppressLint("JavascriptInterface")
    private void chargeWeb(String web) {
        if (webView != null) {

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAppCacheEnabled(false);
            webView.getSettings().setDomStorageEnabled(false);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            webView.setWebViewClient(new MyWebViewClient());
            webView.setWebChromeClient(new MyWebChromeClient());

            webView.clearHistory();
            webView.clearFormData();
            webView.clearCache(true);

            webView.loadUrl(web);


            deleteCache(context);
            onLowMemory();
            //WebView.loadDataWithBaseURL(web,data,"text/html","UTF-8",web);

            DownloadArray=new ArrayList<DownloadInfo>();

            webView.setDownloadListener(
                    new DownloadListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDownloadStart(String url, String userAgent,
                                                    String contentDisposition, String mimeType,
                                                    long contentLength) {


                            if(url.startsWith("data:")){
                                StringTokenizer st = new StringTokenizer(url, ",");
                                String text = st.nextToken();
                                base64 = st.nextToken();

                                if(text.equals("data:application/xml;base64")) {
                                    UrlXml= url;
                                    webView.evaluateJavascript("document.querySelector('[href=\"" + UrlXml + "\"]').download", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String name) {
                                            String nameFile = name.replace("\"", "");
                                            if (UrlXml.startsWith("data:")) {

                                                StringTokenizer st = new StringTokenizer(UrlXml, ",");
                                                String text = st.nextToken();
                                                base64 = st.nextToken();

                                                if (text.equals("data:application/xml;base64")) {

                                                    base64xml = base64;

                                                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                                    String FileName = "Factura_" + timeStamp +".xml";
                                                    filenameXML = isnull(nameFile,FileName);

                                                    //myExternalFilexml = new File(getExternalFilesDir(filepath), filenameXML);
                                                    myExternalFilexml = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), filenameXML);


                                                    try {
                                                        byte[] decodedString = Base64.decode(base64xml, Base64.DEFAULT);
                                                        FileOutputStream fos = new FileOutputStream(myExternalFilexml);
                                                        fos.write(decodedString);
                                                        fos.close();
                                                        MostrarOpcionesDescarga();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    DownloadArray.add(0,new DownloadInfo(base64xml, "application/octet-stream", filenameXML));
                                                    ContadorXML++;
                                                    Log.d("FileName", nameFile); // Prints: {"var1":"variable1","var2":"variable2"}
                                                }

                                            }
                                        }
                                    });
                                }
                                if(text.equals("data:application/pdf;base64")) {
                                    UrlPdf = url;
                                    webView.evaluateJavascript("document.querySelector('[href=\"" + UrlPdf + "\"]').download", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String name) {
                                            String nameFile = name.replace("\"", "");
                                            //filenameXML="Acuse"+String.valueOf(ContadorXML)+".xml";
                                            if (UrlPdf.startsWith("data:")) {

                                                StringTokenizer st = new StringTokenizer(UrlPdf, ",");
                                                String text = st.nextToken();
                                                base64 = st.nextToken();

                                                if (text.equals("data:application/pdf;base64")) {
                                                    base64pdf = base64;

                                                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                                    String FileName = "Factura_" + timeStamp +".pdf" ;
                                                    filename = isnull(nameFile,FileName);
                                                    myExternalFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), filename);
                                                    try {
                                                        byte[] decodedString = Base64.decode(base64pdf, Base64.DEFAULT);
                                                        FileOutputStream fos = new FileOutputStream(myExternalFile);
                                                        fos.write(decodedString);
                                                        fos.close();
                                                        MostrarOpcionesDescarga();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    DownloadArray.add(0,new DownloadInfo(base64pdf, "application/pdf", filename));
                                                    ContadorPdf++;
                                                    Log.d("FileName", nameFile); // Prints: {"var1":"variable1","var2":"variable2"}
                                                }
                                            }
                                        }
                                    });
                                }
                                if(text.equals("data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64")) {
                                    UrlXls = url;
                                    webView.evaluateJavascript("document.querySelector('[href=\"" + UrlXls + "\"]').download", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String name) {
                                            String nameFile = name.replace("\"", "");
                                            if (UrlXls.startsWith("data:")) {

                                                StringTokenizer st = new StringTokenizer(UrlXls, ",");
                                                String text = st.nextToken();
                                                base64 = st.nextToken();
                                                if (text.equals("data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64")) {


                                                    base64xls = base64;

                                                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                                    String FileName = "Reporte_" + timeStamp +".xlsx" ;
                                                    filenameExcel = isnull(FileName,FileName);
                                                    myExternalFilexsl = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), filenameExcel);
                                                    try {
                                                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                                                        FileOutputStream fos = new FileOutputStream(myExternalFilexsl);
                                                        fos.write(decodedString);
                                                        fos.close();
                                                        MostrarOpcionesDescarga();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    DownloadArray.add(0,new DownloadInfo(base64xls, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", filenameExcel));
                                                    Log.d("FileName", nameFile); // Prints: {"var1":"variable1","var2":"variable2"}

                                                }
                                            }
                                        }
                                    });
                                }
                                if (AlertaMostrada == false) {
                                    AlertaMostrada = true;

                                    Dialogo = LayoutInflater.from(context).inflate(R.layout.alerta_descarga, null);
                                    AlertErrordialog = new Dialog(context);
                                    AlertErrordialog.setContentView(Dialogo);
                                    AlertErrordialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    DisplayMetrics displaymetrics = new DisplayMetrics();
                                    AlertErrordialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                                    AlertErrordialog.setCanceledOnTouchOutside(false);
                                    AlertErrordialog.setCancelable(false);
                                    WindowManager.LayoutParams params = AlertErrordialog.getWindow().getAttributes();

                                    params.width = (int) (displaymetrics.widthPixels * 0.95);
                                    params.gravity = Gravity.CENTER;
                                    AlertErrordialog.getWindow().setAttributes(params);


                                    AlertErrordialog.show();
                                    TextView TextoDialogo = Dialogo.findViewById(R.id.txtTextoDialogo);
                                    TextView TextoTitulo = Dialogo.findViewById(R.id.txtSolicitud);
                                    Button btnCancelar = Dialogo.findViewById(R.id.btnCancelar);
                                    Button btnAceptar = Dialogo.findViewById(R.id.btnAceptar);
                                    btnAceptar.setVisibility(View.GONE);
                                    RelativeLayout lyheader = Dialogo.findViewById(R.id.lyheader);


                                    listViewOpcionesDescarga = Dialogo.findViewById(R.id.lvopcionesDescarga);
                                    MostrarOpcionesDescarga();




                                    btnCancelar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertErrordialog.cancel();
                                            DownloadArray = new ArrayList<DownloadInfo>();
                                            AlertaMostrada = false;
                                            ContadorPdf = 1;
                                            ContadorXML = 1;
                                            ContadorExcel = 1;
                                        }
                                    });

                                } else {
                                    MostrarOpcionesDescarga();
                                }
                            }
                        }
                    });

        }
        return;
    }
    public void MostrarOpcionesDescarga( ) {

        CallMethodFromAdapter listenerLectura = new CallMethodFromAdapter() {
            @Override
            public void mtdResultado(String resultado) {
                String NameFile=String.valueOf(DownloadArray.get(Integer.parseInt(resultado)).getDownloadName());
                if(NameFile.endsWith("pdf")) {
                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                        File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
                        File imagePath = new File(storageDir, "");
                        File image = new File(imagePath, NameFile);

                        Uri photoUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() +".provider", image);
                        PackageManager packageManager = getPackageManager();
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(photoUri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //startActivity(intent);
                        intent = Intent.createChooser(intent, "Open File");
                        List list = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);

                        if (list.size() > 0) {
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(MainActivity.this, "Application not found", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "No exiten aplicaciones para abrir el archivo", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(filepath), "application/pdf");
                        intent = Intent.createChooser(intent, "Open File");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }

                if(NameFile.endsWith("xlsx")) {
                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                        File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
                        File imagePath = new File(storageDir, "");
                        File image = new File(imagePath, NameFile);

                        Uri photoUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() +".provider", image);
                        PackageManager packageManager = getPackageManager();
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setDataAndType(photoUri, "application/vnd.ms-excel");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent = Intent.createChooser(intent, "Open File");
                        List list = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);

                        if (list.size() > 0) {
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(MainActivity.this, "Application not found", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "No exiten aplicaciones para abrir el archivo", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(filepath), "application/vnd.ms-excel");
                        intent = Intent.createChooser(intent, "Open File");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                if(NameFile.endsWith("xml")) {
                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
                        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                        File imagePath = new File(storageDir, "");
                        File image = new File(imagePath, NameFile);

                        Uri photoUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() +".provider", image);
                        PackageManager packageManager = getPackageManager();
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setDataAndType(photoUri, "text/plain");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent = Intent.createChooser(intent, "Open File");
                        List list = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);

                        if (list.size() > 0) {
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(MainActivity.this, "Application not found", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "No exiten aplicaciones para abrir el archivo", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(filepath), "text/plain");
                        intent = Intent.createChooser(intent, "Open File");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        };
        class myComparator implements Comparator<DownloadInfo> {
            @Override
            public int compare(DownloadInfo a, DownloadInfo b) {
                String nombrea =  a.getDownloadName();
                StringTokenizer st = new StringTokenizer(nombrea, ".");
                String nombreaa = st.nextToken();
                String tipoa = st.nextToken();
                String nombreb =  b.getDownloadName();
                StringTokenizer stb = new StringTokenizer(nombreb, ".");
                String nombrebb = stb.nextToken();
                String tipob = stb.nextToken();
                return tipoa.compareToIgnoreCase(tipob);
            }
        }
        //Collections.reverse(DownloadArray);
        Collections.sort(DownloadArray, new myComparator());
        OpcionesDescargaAdapter OpcionesDescargaAdapter = new OpcionesDescargaAdapter(MainActivity.this, DownloadArray,listViewOpcionesDescarga,listenerLectura);
        listViewOpcionesDescarga.setAdapter(OpcionesDescargaAdapter);
        OpcionesDescargaAdapter.notifyDataSetChanged();
        //new setListViewHeightBasedOnItems(listViewPersonalAsignado);
        listViewOpcionesDescarga.setOnItemClickListener(MainActivity.this);
        listViewOpcionesDescarga.refreshDrawableState();



    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.lvopcionesDescarga) {
            String NameFile=String.valueOf(DownloadArray.get(position).getDownloadName());
            if(NameFile.endsWith("pdf")) {
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
                    File imagePath = new File(storageDir, "");
                    File image = new File(imagePath, NameFile);

                    Uri photoUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() +".provider", image);
                    PackageManager packageManager = getPackageManager();
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(photoUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //startActivity(intent);
                    intent = Intent.createChooser(intent, "Open File");
                    List list = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);

                    if (list.size() > 0) {
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainActivity.this, "Application not found", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "No exiten aplicaciones para abrir el archivo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(filepath), "application/pdf");
                    intent = Intent.createChooser(intent, "Open File");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }if(NameFile.endsWith("xlsx")) {
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
                    File imagePath = new File(storageDir, "");
                    File image = new File(imagePath, NameFile);

                    Uri photoUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() +".provider", image);
                    PackageManager packageManager = getPackageManager();
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setDataAndType(photoUri, "application/vnd.ms-excel");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent = Intent.createChooser(intent, "Open File");
                    List list = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);

                    if (list.size() > 0) {
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainActivity.this, "Application not found", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "No exiten aplicaciones para abrir el archivo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(filepath), "application/vnd.ms-excel");
                    intent = Intent.createChooser(intent, "Open File");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }if(NameFile.endsWith("xml")) {
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
                    File imagePath = new File(storageDir, "");
                    File image = new File(imagePath, NameFile);

                    Uri photoUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() +".provider", image);
                    PackageManager packageManager = getPackageManager();
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setDataAndType(photoUri, "text/plain");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent = Intent.createChooser(intent, "Open File");
                    List list = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);

                    if (list.size() > 0) {
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainActivity.this, "Application not found", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "No exiten aplicaciones para abrir el archivo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(filepath), "text/plain");
                    intent = Intent.createChooser(intent, "Open File");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }


    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private class MyWebChromeClient extends WebChromeClient{

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            Intent intent= new Intent();
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            if (Build.VERSION.SDK_INT>=21) {
                if (fileChooserParams.getAcceptTypes()!=null)
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, fileChooserParams.getAcceptTypes());
            }
            MainActivity.this.filePathCallback = filePathCallback;
            startActivityForResult(Intent.createChooser(intent, "Select file"), REQUEST_FILE);
            return true;
        }
    }
    // Tarea repetitiva en segundo plano. Se encarga de comprobar si la conexión se pierde o no.
    private class networkConnected extends TimerTask {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Comprobar si existe o no conexión a internet
                    ConnectivityManager connectivityManager = (ConnectivityManager)
                          getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                    if (info == null || !info.isConnected() || !info.isAvailable()) {
                      connectNew = false; // Desconectado
                    } else {
                      connectNew = true; // Conectado
                    }

                    if (connectNew != connectOld) {
                        if (!connectNew) { //Desconectado.
                            imageConnect.setVisibility(View.VISIBLE);
                            msjCon.setVisibility(View.VISIBLE);
                            msjConnection.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                        } else { //Conectado.
                            imageConnect.setVisibility(View.INVISIBLE);
                            msjCon.setVisibility(View.INVISIBLE);
                            msjConnection.setVisibility(View.INVISIBLE);
                            webView.setVisibility(View.VISIBLE);
                            chargeWeb(url);
                        }
                    }
                    connectOld = connectNew;
                }
            });
        }

    }


    public class MyWebViewClient extends WebViewClient {
        private Uri mCapturedImageURI;
        private int FILECHOOSER_RESULTCODE=11;
        private ValueCallback<Uri> mUploadMessage;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            deleteCache(context);


            if (url.startsWith("http://api.whatsapp.com")) {
                view.stopLoading();
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (android.content.ActivityNotFoundException ex) {

                    String MakeShortText = "Whatsapp have not been installed";

                    Toast.makeText(MainActivity.this, MakeShortText, Toast.LENGTH_SHORT).show();
                }


            }else{
                view.getSettings().setJavaScriptEnabled(true);
                view.loadUrl(url);
                view.loadUrl( "javascript:window.location.reload( true )");
            }
            return true;
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            WebResourceResponse returnResponse = null;
            onLowMemory();
            deleteCache(context);
            return super.shouldInterceptRequest(view, request);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            //tvError.setVisibility(View.VISIBLE);
            //mWebView.setVisibility(View.GONE);
            //if(dialog.isShowing())dialog.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);

           //if(objCommon.isOnline(context))
           //    tvError.setVisibility(View.GONE);
           //else
           //    tvError.setVisibility(View.VISIBLE);
        }



        @Override
        public void onPageFinished(WebView view, String url) {
            if(url.equals("https://facturasff.com/Agenda/Medico") || url.startsWith("https://facturasff.com/Account/LogOn") || url.startsWith("https://192.168.1.93:85")){

                new android.os.Handler().postDelayed(new Runnable() {
                    public void run() {
                        injectCSS();
                        SplashLayout.animate()
                            .translationY(0)
                            .alpha(0.0f)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    SplashLayout.setVisibility(View.GONE);
                                }
                            });
                    }
                },200);
            }
            super.onPageFinished(view, url);
        }

    }

    @Override
    protected void onDestroy () {
        if (webView != null)
            webView.destroy();
        super.onDestroy();
    }

    private void injectCSS() {
        try {
            InputStream inputStream = getAssets().open("style.css");
            int bufferSize = 1024;
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public String isnull(String campo, String resp){
        if(campo ==null || campo.isEmpty() || campo.equals("")){
            return resp;
        }
        return campo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FILE) {
                if (filePathCallback != null) {
                    if (data.getData() != null) {
                        filePathCallback.onReceiveValue(new Uri[]{data.getData()});
                        filePathCallback = null;
                    }
                }
            }
        }
    }


    static int clearCacheFolder(final File dir, final int numDays) {

        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {

                    //first delete subdirectories recursively
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }

                    //then delete the files and subdirectories in this dir
                    //only empty directories can be deleted, so subdirs have been done first
                    if (child.lastModified() < new Date().getTime() - numDays * DateUtils.DAY_IN_MILLIS) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            }
            catch(Exception e) {
                Log.e(TAG, String.format("Failed to clean the cache, error %s", e.getMessage()));
            }
        }
        return deletedFiles;
    }

    /*
     * Delete the files older than numDays days from the application cache
     * 0 means all files.
     */
    public static void clearCache(final Context context, final int numDays) {
        Log.i(TAG, String.format("Starting cache prune, deleting files older than %d days", numDays));
        int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
        Log.i(TAG, String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}


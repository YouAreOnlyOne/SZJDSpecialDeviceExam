package com.example.frank.jinding.UI.CheckerActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.example.frank.jinding.Conf.CheckControl;
import com.example.frank.jinding.Conf.URLConfig;
import com.example.frank.jinding.R;
import com.example.frank.jinding.Service.ApiService;
import com.example.frank.jinding.UI.SelectPicture.MyPhotoActivity;
import com.example.frank.jinding.Upload.FtpClientUpload;
import com.example.frank.jinding.Upload.FtpUpload;
import com.example.frank.jinding.Utils.CameraPermissionCompat;
import com.example.frank.jinding.Utils.SaveImage;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import org.angmarch.views.NiceSpinner;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SelectEquipment extends AppCompatActivity {

    private Button add_infomation,add_photo,upload,post,addopinion,lookresult;
    private ArrayAdapter<String> spadapter;
    private List<String> ls = new ArrayList<String>();
    private List<String> list = new ArrayList<String>();
    private org.angmarch.views.NiceSpinner mSpinner;
    // private ArrayAdapter spinnerAdapter;
    private int spinnerSelectedItem=1;
    private List<String>NiceSpinner;
    public static ArrayList<String> mDataList = new ArrayList<>();//存储选取图片路径
    private ImageButton back;
    private TextView title ,device;
    private ListView lv_tasksss;
    private int environTag=0,currentCheckTime=0,selectedCheckTime=0;
    private String deviceinfo="",isMainChecker="",consignmentId="",orderId="",deviceId="",submission_id="";
    private  AlertDialog processDialogRequest;
    private  boolean dirurl=false;
    public static int sum_tag=0,file_tag=0,text_tag=0;
    private MyAdapter mAdapter;
    FtpUpload ff=new FtpUpload();
    private ProgressBar upload_wait;
    private  String path = Environment.getExternalStorageDirectory() + "/Luban/image/"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_equipment);

       /* StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
*/
        View processViewRequest = View.inflate(SelectEquipment.this, R.layout.simple_processbar, null);
        processDialogRequest= new AlertDialog.Builder(SelectEquipment.this).create();
        processDialogRequest.setView(processViewRequest);

        init();


        device.setText("正在检测设备："+deviceinfo);
        //标题栏设置
        title.setText("设备检验");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View processView = View.inflate(SelectEquipment.this, R.layout.simple_processbar, null);
        AlertDialog processDialogPic = new AlertDialog.Builder(this).create();
        processDialogPic.setView(processView);



        //判断那一个人进行这一个设备的检验意见填写
        /*if (!isMainChecker.equals("true")){
            addopinion.setVisibility(addopinion.INVISIBLE);
            lookresult.setVisibility(lookresult.INVISIBLE);
        }*/


      /*  add_infomation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //打印Button的点击信息
                new  AlertDialog.Builder(SelectEquipment.this)
                        .setTitle("系统提示")
                        .setMessage("\n添加成功！")
                        .setPositiveButton("确定",
                                new  DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public  void  onClick(DialogInterface dialog, int  which)
                                    {
                                        opinion.setText("");
                                    }
                                }).show();
            }
        });*/


        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                if (sum_tag>0&&sum_tag==file_tag&&sum_tag==text_tag){
                    //upload_wait.setVisibility(View.INVISIBLE);
                    processDialogPic.dismiss();
                    Toast.makeText(SelectEquipment.this, "上传成功", Toast.LENGTH_SHORT).show();
                    sum_tag=0;file_tag=0;text_tag=0;

                }else {
                    handler.postDelayed(this,2000);
                }

            }
        };



        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                LinearLayout linearLayout=new LinearLayout(SelectEquipment.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                Button photo_machine=new Button(SelectEquipment.this);
                photo_machine.setText("相机拍照");
                Button photo_picture=new Button(SelectEquipment.this);
                photo_picture.setText("本地照片");
                linearLayout.addView(photo_machine);
                linearLayout.addView(photo_picture);


                AlertDialog photo_dialog=new  AlertDialog.Builder(SelectEquipment.this).create();
                photo_dialog.setView(linearLayout);
                photo_dialog.show();

                photo_machine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photo_dialog.dismiss();
                        Boolean permission=CameraPermissionCompat.checkCameraPermission(SelectEquipment.this, new CameraPermissionCompat.OnCameraPermissionListener() {
                            @Override
                            public void onGrantResult(boolean granted) {

                                Log.i("相机权限：",granted+"");
                            }
                        });

                        if (permission) {
                            Intent intent = new Intent(SelectEquipment.this, Equipment_Recorde.class);
                            //startActivity(intent);
                            startActivityForResult(intent, 5201);
                        }
                        else {
                            new AlertDialog.Builder(SelectEquipment.this).setTitle("系统提示").setMessage("您还没有给该应用赋予拍照的权限，请前往手机设置里面手动赋予该应用相机权限").show();
                        }
                    }
                });

                photo_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photo_dialog.dismiss();
                        Intent intent = new Intent(SelectEquipment.this, MyPhotoActivity.class);
                        //startActivity(intent);
                        MyPhotoActivity.finishPhotoSelect=false;
                        startActivityForResult(intent, 7777);
                    }
                });



            }
        });

        // /*为ListView添加点击事件*/


        lv_tasksss.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (selectedCheckTime==currentCheckTime) {
                    String pic_description = mAdapter.listItem.get(arg2).get("ItemText").toString();
                    String pic_url = mAdapter.listItem.get(arg2).get("ItemImage").toString();
                    String pic_tag = mAdapter.listItem.get(arg2).get("Tag").toString();
                    EditText et = new EditText(SelectEquipment.this);
                    if (mAdapter.listItem.get(arg2).get("ItemText").toString().equals("请填写照片相关描述")) {
                        et.setHint(mAdapter.listItem.get(arg2).get("ItemText").toString());
                    } else {
                        et.setText(mAdapter.listItem.get(arg2).get("ItemText").toString());
                    }
                    new AlertDialog.Builder(SelectEquipment.this)
                            .setMessage("请修改新的检验情况说明：")
                            .setView(et)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (mAdapter.listItem.get(arg2).get("Tag").toString().trim().equals("0")) {
                                                HashMap<String, Object> map = new HashMap<String, Object>();
                                                map.put("ItemImage", pic_url);
                                                map.put("ItemText", et.getText());
                                                map.put("Tag", pic_tag);
                                                mAdapter.listItem.remove(arg2);
                                                mAdapter.listItem.add(map);
                                                mAdapter.notifyDataSetChanged();
                                                Toast.makeText(SelectEquipment.this, "修改成功", Toast.LENGTH_SHORT).show();
                                            } else if (!et.getText().toString().equals(pic_description) && mAdapter.listItem.get(arg2).get("Tag").toString().trim().equals("1")) {
                                                String filename = mAdapter.listItem.get(arg2).get("ItemImage").toString();
                                                String datafile = filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf("."));

                                                String picDescrip = et.getText().toString();
                                                HashMap<String, String> map_data = new HashMap<>();
                                                map_data.put("orderId", orderId);
                                                map_data.put("consignmentId", consignmentId);
                                                map_data.put("deviceId", deviceId);
                                                map_data.put("datafile", datafile);
                                                map_data.put("picDescrip", picDescrip);
                                                Map<String, Object> paremetes = new HashMap<>();
                                                paremetes.put("data", JSON.toJSONString(map_data));

                                                ApiService.GetString(SelectEquipment.this, "modifyDescription", paremetes, new RxStringCallback() {
                                                    boolean flag = false;

                                                    @Override
                                                    public void onNext(Object tag, String response) {
                                                        if (response.trim().equals("修改成功！")) {
                                                            HashMap<String, Object> map = new HashMap<String, Object>();
                                                            map.put("ItemImage", pic_url);
                                                            map.put("ItemText", et.getText());
                                                            map.put("Tag", pic_tag);
                                                            mAdapter.listItem.remove(arg2);
                                                            mAdapter.listItem.add(map);
                                                            mAdapter.notifyDataSetChanged();
                                                            Toast.makeText(SelectEquipment.this, "修改成功", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Object tag, Throwable e) {
                                                        Toast.makeText(SelectEquipment.this, "修改失败" + e, Toast.LENGTH_SHORT).show();


                                                    }

                                                    @Override
                                                    public void onCancel(Object tag, Throwable e) {
                                                        Toast.makeText(SelectEquipment.this, "修改失败" + e, Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                            }


                                        }
                                    }).show();
                }else {
                    Toast.makeText(SelectEquipment.this,"非本次检验现场信息，没有权限修改",Toast.LENGTH_SHORT).show();
                }

            }
        });




        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!dirurl){
                    Toast.makeText(SelectEquipment.this,"正在设置检验环境",Toast.LENGTH_SHORT).show();
                    dir_url();


                }
                    new AlertDialog.Builder(SelectEquipment.this)
                            .setMessage("您是否确定上传本次检验记录？\n\n如果上传完成，可以填写检验意见！")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            sum_tag=0;file_tag=0;text_tag=0;
                                            for (int i = 0; i < mAdapter.listItem.size(); i++) {

                                                if (mAdapter.listItem.get(i).get("Tag").toString().equals("0")) {
                                                    sum_tag++;
                                                }
                                            }
                                            if (sum_tag>0){
                                                //如果是本地图片，就开始向服务器上传照片
                                                //upload_wait.setVisibility(View.VISIBLE);
                                                processDialogPic.show();
                                                handler.postDelayed(runnable,2000);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {


                                                            for (int i = 0; i < mAdapter.listItem.size(); i++) {

                                                                if (mAdapter.listItem.get(i).get("Tag").toString().equals("0")) {
                                                                    //上传文件到服务器
                                                                    String eqfilename = mAdapter.listItem.get(i).get("ItemImage").toString();
                                                                    String datafilename = eqfilename.substring(eqfilename.lastIndexOf("/") + 1, eqfilename.length());
                                                                    FtpClientUpload.UploadFile(eqfilename, orderId + "/" + consignmentId + "/" + deviceId + "/", SelectEquipment.this, datafilename);
                                                                    //ff.upload(SelectEquipment.this,orderId+"/"+consignmentId+"/"+deviceId,mAdapter.listItem.get(i).get("ItemImage").toString());


                                                                    //上传文字描述到服务器
                                                                    HashMap<String,String> map_data=new HashMap<>();
                                                                    String filename = mAdapter.listItem.get(i).get("ItemImage").toString();
                                                                    // String dd = orderId + "#" + consignmentId + "#" + deviceId + "#" + filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")) + "#" + mAdapter.listItem.get(i).get("ItemText").toString();
                                                                    map_data.put("orderId",orderId);
                                                                    map_data.put("submissionId",submission_id);
                                                                    map_data.put("consignmentId",consignmentId);
                                                                    map_data.put("deviceId",deviceId);
                                                                    map_data.put("picName",filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")));
                                                                    map_data.put("picDescrip",mAdapter.listItem.get(i).get("ItemText").toString());

                                                                    Map<String, Object> paremetes = new HashMap<>();
                                                                    paremetes.put("data", JSON.toJSONString(map_data));
                                                                    ApiService.GetString(SelectEquipment.this, "pictureInfomation", paremetes, new RxStringCallback() {
                                                                        boolean flag = false;

                                                                        @Override
                                                                        public void onNext(Object tag, String response) {

                                                                            if (response.trim().equals("上传成功！")) {
                                                                                text_tag++;
                                                                                //  Toast.makeText(SelectEquipment.this, "上传成功", Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onError(Object tag, Throwable e) {
                                                                            Toast.makeText(SelectEquipment.this, "" + e, Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        @Override
                                                                        public void onCancel(Object tag, Throwable e) {
                                                                            Toast.makeText(SelectEquipment.this, "" + e, Toast.LENGTH_SHORT).show();

                                                                        }


                                                                    });

                                                                }

                                                            }


                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).start();

                                            }else {
                                                //如果是网络图片就不再上传
                                                Toast.makeText(SelectEquipment.this, "图片均已上传成功", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }).show();


                
              /*  new  AlertDialog.Builder(SelectEquipment.this)
                        .setTitle("系统提示")
                        .setMessage("关于此型号设备是否检测完成？点击“确定”上传添加的所有文字信息和照片信息，并进行撰写报告。点击“取消”继续添加设备相关信息！")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定",
                                new  DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public  void  onClick(DialogInterface dialog, int  which)
                                    {


                                        CheckInfo.listItem=null;
                                        Toast.makeText(SelectEquipment.this,"信息上传成功！",Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(SelectEquipment.this,CheckOpinion.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();*/



              }
        });






        addopinion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isMainChecker.equals("true")) {
                    new AlertDialog.Builder(SelectEquipment.this)
                            .setMessage("撰写报告前请点击“上传保存”，以免信息丢失！如果您确定检测已经完成，点击“确定”进行撰写检测意见，点击“取消”继续进行相关检测！")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(SelectEquipment.this, CheckOpinion.class);
                                            intent.putExtra("submission_id", submission_id);
                                            intent.putExtra("orderId", orderId);
                                            intent.putExtra("deviceId", deviceId);
                                            intent.putExtra("consignmentId", consignmentId);
                                            startActivityForResult(intent, 123);

                                            //startActivity(intent);
                                            //finish();


                                        }
                                    }).show();
                }else {
                    new AlertDialog.Builder(SelectEquipment.this)
                            .setMessage("请与主检验员协商，协商之后由主检填写检验意见，上传之后您可以查看检验意见！")
                            .setNegativeButton("取消",null).show();
                }


            }
        });


        lookresult.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent intent=new Intent(SelectEquipment.this,OpinionResultActivity.class);
                intent.putExtra("submission_id", submission_id);
                intent.putExtra("orderId", orderId);
                intent.putExtra("deviceId",deviceId);
                startActivity(intent);

            }
        });






    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==5201&& CheckControl.isPhoto) {
            CheckControl.isPhoto=false;

           String dd=data.getStringExtra("dd");
            String da[]=dd.split("##");
            System.out.println("返回的数据："+dd);
            if (da[0].equals("true")){

                HashMap<String, Object> map=new HashMap<>();
                map.put("ItemImage", da[1]);
                map.put("ItemText", da[2]);
                map.put("Tag",da[3]);
                mAdapter.listItem.add(map);
                //Toast.makeText(SelectEquipment.this,map.get("ItemImage").toString(),Toast.LENGTH_SHORT).show();
               // mAdapter.notifyDataSetChanged();

                //new AlertDialog.Builder(SelectEquipment.this).setMessage(map.get("ItemImage")+"=="+map.get("ItemText")+"=="+map.get("Tag")).show();
                mAdapter.notifyDataSetChanged();

            }


        }



        if (requestCode ==7777) {
            Random random=new Random();
            for (int i=0;i<mDataList.size();i++){
                System.out.println("返回的路径："+mDataList.get(i));
                //设置自定义照片的名字

                String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+random.nextInt(1000);
                String tem_filename=path  + fileName + ".jpg";
                SaveImage.saveBitmap(mDataList.get(i),fileName);

                HashMap<String, Object> map=new HashMap<>();
                map.put("ItemImage", tem_filename);
                map.put("ItemText", "请填写照片相关描述");
                map.put("Tag","0");
                mAdapter.listItem.add(map);

            }
            mDataList.clear();
            mAdapter.notifyDataSetChanged();

        }


        if (requestCode ==123&& CheckControl.device_finish){
            System.out.println("order=="+CheckControl.order_finish);
            System.out.println("protocol=="+CheckControl.protocol_finish);
            System.out.println("device=="+CheckControl.device_finish);
            CheckControl.device_finish=false;
            finish();
        }


    }



    private void init(){

        Intent intent=getIntent();
        deviceinfo=intent.getStringExtra("device");
        isMainChecker=intent.getStringExtra("isMainChecker");
        orderId=intent.getStringExtra("orderId");
        deviceId=intent.getStringExtra("deviceId");
        submission_id=intent.getStringExtra("submission_id");
        consignmentId=intent.getStringExtra("consignmentId");

        add_photo=(Button)this.findViewById(R.id.add_photo);
        addopinion=(Button)this.findViewById(R.id.add_opinion);
        lookresult=(Button)this.findViewById(R.id.look_result);
        upload=(Button)this.findViewById(R.id.other_infomation);
        post=(Button)this.findViewById(R.id.work_end);

        mSpinner=(NiceSpinner)this.findViewById(R.id.check_picture_scene_spinner);

        back=(ImageButton)this.findViewById(R.id.titleback);
        title=(TextView)this.findViewById(R.id.titleplain);

        upload_wait=(ProgressBar)this.findViewById(R.id.prgress_file_upload_wait);
        device=(TextView)this.findViewById(R.id.textView49);

        lv_tasksss=(ListView)this.findViewById(R.id.lv_equipment_situation);
        mAdapter = new MyAdapter(this);//得到一个MyAdapter对象
        lv_tasksss.setAdapter(mAdapter);//为ListView绑定Adapter

        File filecname = new File(path);
        filecname.mkdirs();// 创建文件夹

        if (isNetworkAvalible(SelectEquipment.this)){
            dir_url();
        }else {
           checkNetwork(SelectEquipment.this);
        }

        //ff.download(SelectEquipment.this,orderId+"/"+deviceId+"/","20180103_040520"+".jpg");


        mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.e("检验次数",arg2+"");
                selectedCheckTime=arg2;
                getCheckDetails(arg2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void getCheckDetails(int recheckSeq){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                   // processDialogRequest.show();
                    mAdapter.listItem.clear();
                    HashMap<String,String> map_data=new HashMap<>();
                    map_data.put("orderId",orderId);
                    map_data.put("consignmentId",consignmentId);
                    map_data.put("deviceId",deviceId);
                    map_data.put("recheckSeq",recheckSeq+"");
                    Map<String, Object> paremetes = new HashMap<>();
                    paremetes.put("data",JSON.toJSONString(map_data));
                    ApiService.GetString(SelectEquipment.this, "getOrderCheckDetails", paremetes, new RxStringCallback() {
                        boolean flag = false;

                        @Override
                        public void onNext(Object tag, String response) {


                            //refreshLayout.setRefreshing(false);
                            if (!response.contains("获取失败")&&!response.contains("session为空")) {
                                //Toast.makeText(SelectEquipment.this, "获取到的数据：" + response, Toast.LENGTH_SHORT).show();
                                //String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
                                String data[]=response.split("##");
                                for (int i=0;i+1<data.length;i+=2){
                                    //ff.download(SelectEquipment.this,orderId+"/"+consignmentId+"/"+deviceId+"/",data[i]+".jpg");
                                    String imgurl=URLConfig.CompanyURL+orderId+"/"+consignmentId+"/"+deviceId+"/"+data[i]+".jpg";

                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("ItemImage", imgurl);
                                    //map.put("ItemImage", path+data[i]+".jpg");
                                    map.put("ItemText", data[i+1]);
                                    map.put("Tag","1");
                                    mAdapter.listItem.add(map);
                                }



                            }else {
                                Toast.makeText(SelectEquipment.this, response, Toast.LENGTH_SHORT).show();
                            }
                            //processDialogRequest.dismiss();
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Object tag, Throwable e) {
                            Toast.makeText(SelectEquipment.this, "获取失败" + e, Toast.LENGTH_SHORT).show();
                            //refreshLayout.setRefreshing(false);
                           // processDialogRequest.dismiss();

                        }

                        @Override
                        public void onCancel(Object tag, Throwable e) {
                            Toast.makeText(SelectEquipment.this, "获取失败" + e, Toast.LENGTH_SHORT).show();
                            //refreshLayout.setRefreshing(false);
                           // processDialogRequest.dismiss();
                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }


    private void getRecheckPhoto(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    HashMap<String,String> map_data=new HashMap<>();
                    map_data.put("orderId",orderId);
                    map_data.put("consignmentId",consignmentId);
                    map_data.put("deviceId",deviceId);
                    Map<String, Object> paremetes = new HashMap<>();
                    paremetes.put("data",JSON.toJSONString(map_data));
                    ApiService.GetString(SelectEquipment.this, "getCheckPhotoByTimes", paremetes, new RxStringCallback() {
                        boolean flag = false;

                        @Override
                        public void onNext(Object tag, String response) {


                        }

                        @Override
                        public void onError(Object tag, Throwable e) {
                            Toast.makeText(SelectEquipment.this, "获取失败" + e, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancel(Object tag, Throwable e) {
                            Toast.makeText(SelectEquipment.this, "获取失败" + e, Toast.LENGTH_SHORT).show();
                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }




    private void dir_url(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    HashMap<String,String> map_data=new HashMap<>();
                    map_data.put("orderId",orderId);
                    map_data.put("url",URLConfig.CompanyURL+orderId+"/");
                    map_data.put("device_id",deviceId);
                    map_data.put("submission_id",submission_id);
                    Map<String, Object> paremetes = new HashMap<>();
                    paremetes.put("data", JSON.toJSONString(map_data));
                    upload_wait.setVisibility(View.VISIBLE);
                    //processDialogRequest.show();
                    ApiService.GetString(SelectEquipment.this, "orderDetailsUrl", paremetes, new RxStringCallback() {
                        boolean flag = false;

                        @Override
                        public void onNext(Object tag, String response) {

                            Log.i("环境信息",response);
                            HashMap<String,String> map_data=JSON.parseObject(response,new TypeReference<HashMap<String,String>>(){});

                            if (map_data!=null) {

                                String dir_url =map_data.get("dir_url");
                                String exam_result=map_data.get("exam_result");
                                String erro=map_data.get("erro");
                                String login=map_data.get("login");
                                int recheckSeq=Integer.parseInt(map_data.get("recheckSeq"));
                                currentCheckTime=recheckSeq;
                                selectedCheckTime=recheckSeq;
                                List<String> spinnerList=new ArrayList<>();
                                for (int k=0;k<=recheckSeq;k++){
                                    spinnerList.add("第"+(k+1)+"次检验");
                                }
                                NiceSpinner = new LinkedList<String>(spinnerList);
                                mSpinner.attachDataSource(NiceSpinner);
                                mSpinner.setSelectedIndex(recheckSeq);

                                if (dir_url!=null&&exam_result!=null) {
                                    upload_wait.setVisibility(View.INVISIBLE);
                                    dirurl = true;
                                    Toast.makeText(SelectEquipment.this, "检验环境设置成功", Toast.LENGTH_SHORT).show();
                                    if (exam_result.equals("true")) {
                                        add_photo.setEnabled(false);
                                        addopinion.setEnabled(false);
                                        upload.setEnabled(false);
                                    } else {
                                        add_photo.setEnabled(true);
                                        addopinion.setEnabled(true);
                                        upload.setEnabled(true);
                                    }

                                } else if (erro.equals("false")) {

                                    dirurl = false;
                                    //Toast.makeText(SelectEquipment.this, "查询失败，请返回上一步重新进入", Toast.LENGTH_SHORT).show();
                                    add_photo.setEnabled(false);
                                    addopinion.setEnabled(false);
                                    upload.setEnabled(false);
                                    if (environTag<5){
                                        environTag++;
                                       // processDialogRequest.dismiss();
                                        dir_url();
                                        return;
                                    }
                                } else if (login.equals("true")){
                                    //Toast.makeText(SelectEquipment.this, "查询失败，需要重新登录账号", Toast.LENGTH_SHORT).show();
                                    dirurl = false;
                                    add_photo.setEnabled(false);
                                    addopinion.setEnabled(false);
                                    upload.setEnabled(false);
                                    if (environTag<5){
                                        environTag++;
                                      //  processDialogRequest.dismiss();
                                        dir_url();
                                        return;
                                    }
                                }
                            }else {
                                if (environTag<5){
                                    environTag++;
                                  //  processDialogRequest.dismiss();
                                    dir_url();
                                    return;
                                }
                            }

                            if (dirurl){
                                upload_wait.setVisibility(View.INVISIBLE);
                               // processDialogRequest.dismiss();
                                getCheckDetails(selectedCheckTime);
                            }
                            if (environTag>=5){
                                upload_wait.setVisibility(View.INVISIBLE);
                                Toast.makeText(SelectEquipment.this, "信息加载失败，请返回后重试", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                           // processDialogRequest.dismiss();
                        }

                        @Override
                        public void onError(Object tag, Throwable e) {
                           // processDialogRequest.dismiss();
                            //Toast.makeText(SelectEquipment.this, "操作太快，数据请求没有加载。\n请返回上一步，再进入即可" , Toast.LENGTH_SHORT).show();
                            add_photo.setEnabled(false);
                            addopinion.setEnabled(false);
                            upload.setEnabled(false);
                            if (environTag<5){
                                environTag++;
                              //  processDialogRequest.dismiss();
                                dir_url();
                            }
                            if (environTag>=5){
                                upload_wait.setVisibility(View.INVISIBLE);
                                Toast.makeText(SelectEquipment.this, "信息加载失败，请返回后重试", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }

                        @Override
                        public void onCancel(Object tag, Throwable e) {
                            //processDialogRequest.dismiss();
                            //Toast.makeText(SelectEquipment.this, "操作太快，数据请求没有加载。\n请返回上一步，再进入即可", Toast.LENGTH_SHORT).show();
                            add_photo.setEnabled(false);
                            addopinion.setEnabled(false);
                            upload.setEnabled(false);
                            if (environTag<5){
                                environTag++;
                                //processDialogRequest.dismiss();
                                dir_url();
                            }
                            if (environTag>=5){
                                upload_wait.setVisibility(View.INVISIBLE);
                                Toast.makeText(SelectEquipment.this, "信息加载失败，请返回后重试", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });





                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }



    public static boolean isNetworkAvalible(Context context) {
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();

            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 如果没有网络，则弹出网络设置对话框
    public  void checkNetwork(final Activity activity) {
        if (!isNetworkAvalible(activity)) {
            TextView msg = new TextView(activity);
            msg.setText("当前没有可以使用的网络，请设置网络！");
            new AlertDialog.Builder(activity)
                    .setIcon(R.drawable.network_show)
                    .setTitle("网络状态提示")
                    .setView(msg)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // 跳转到设置界面
                                    activity.startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
                                    SelectEquipment.this.finish();
                                }
                            })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SelectEquipment.this.finish();
                        }
                    }).create().show();
        }
        return;
    }




    private class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
        //ArrayList<HashMap<String, Object>> listItem = CheckInfo.listItem;
        private  ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();
        /*构造函数*/
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public int getCount() {

            return listItem.size();//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        /*书中详细解释该方法*/
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            //观察convertView随ListView滚动情况
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.inspection_report,null);
                holder = new ViewHolder();
                /*得到各个控件的对象*/

                holder.title = (TextView) convertView.findViewById(R.id.textView33);
                holder.delete=(Button)convertView.findViewById(R.id.button60);
                holder.modify=(Button)convertView.findViewById(R.id.button61);
                holder.pic = (ImageView) convertView.findViewById(R.id.imageView5);

                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/


            /*holder.title.setText(getDate().get(position).get("ItemText").toString());
            holder.pic.setImageResource(Integer.parseInt(getDate().get(position).get("ItemImage").toString()));*/

            //holder.pic.setImageURI(Uri.parse(getDate().get(position).get("ItemImg").toString()));
            /*Bitmap bm = BitmapFactory.decodeFile(listItem.get(position).get("ItemImage").toString());
            holder.pic.setImageBitmap(bm);*/

            //文字处理
            //holder.title.setText(listItem.get(position).get("ItemText").toString());
            String tem_str=listItem.get(position).get("ItemText").toString();
            if (tem_str.equals("请填写照片相关描述")){
                holder.title.setTextColor(Color.RED);
            }else {
                holder.title.setTextColor(Color.BLACK);
            }
            holder.title.setText(tem_str);




            //图片处理

            /*Bitmap bm = BitmapFactory.decodeFile(listItem.get(position).get("ItemImage").toString());
            holder.pic.setImageBitmap(bm);*/


            if (listItem.get(position).get("Tag").toString().trim().equals("1")){
                Glide.with(SelectEquipment.this).load(listItem.get(position).get("ItemImage").toString()).into(holder.pic);

                /*Bitmap bm=GetThumbnail.getImageThumbnail(SelectEquipment.this,getContentResolver(),listItem.get(position).get("ItemImage").toString());
                holder.pic.setImageBitmap(bm);*/
            }else if (listItem.get(position).get("Tag").toString().trim().equals("0")){

                Bitmap bm = BitmapFactory.decodeFile(listItem.get(position).get("ItemImage").toString());
                holder.pic.setImageBitmap(bm);

            }

            if (selectedCheckTime==currentCheckTime){
                holder.delete.setVisibility(View.VISIBLE);
            }else {
                holder.delete.setVisibility(View.INVISIBLE);
            }



            holder.pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // final SelectEquipment.ViewHolder holder;
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.dialog_report, (ViewGroup) findViewById(R.id.dalog_report));
                    final ImageView img = (ImageView) layout.findViewById(R.id.imageView6);
                    final TextView textv =(TextView) layout.findViewById(R.id.textView34);

                    //img.setImageResource(R.drawable.report);

                    //获取imageview中显示的图片
                    /*holder.pic.buildDrawingCache(true);
                    holder.pic.buildDrawingCache();
                    Bitmap bitmap = holder.pic.getDrawingCache();
                    img.setImageBitmap(bitmap);
                    holder.pic.setDrawingCacheEnabled(false);*/

                    String picname=mAdapter.listItem.get(position).get("ItemImage").toString();

                    /*final Bitmap bitmap = loadingImageBitmap(picname);
                    img.setImageBitmap(bitmap);*/

                    if (listItem.get(position).get("Tag").toString().trim().equals("1")){
                        Glide.with(SelectEquipment.this).load(picname).into(img);
                    }else if (listItem.get(position).get("Tag").toString().trim().equals("0")){

                        final Bitmap bitmap = loadingImageBitmap(picname);
                        img.setImageBitmap(bitmap);

                    }


                    textv.setText(holder.title.getText());
                    new AlertDialog.Builder(SelectEquipment.this).setTitle("详细信息").setView(layout)
                            .setPositiveButton("确定", new  DialogInterface.OnClickListener()
                            {
                                @Override
                                public  void  onClick(DialogInterface dialog, int  which)
                                {

                                }
                            })
                            .setNegativeButton("取消", new  DialogInterface.OnClickListener()
                            {
                                @Override
                                public  void  onClick(DialogInterface dialog, int  which)
                                {
                                }
                            }).show();
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new  AlertDialog.Builder(SelectEquipment.this)
                            .setMessage("您确定删除本条检测记录吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("确定",
                                    new  DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public  void  onClick(DialogInterface dialog, int  which)
                                        {


                                            if (mAdapter.listItem.get(position).get("Tag").equals("0")){
                                                mAdapter.listItem.remove(position);
                                                mAdapter.notifyDataSetChanged();
                                                Toast.makeText(SelectEquipment.this,"删除成功",Toast.LENGTH_SHORT).show();

                                            }else if (mAdapter.listItem.get(position).get("Tag").equals("1")){
                                                try {

                                                    HashMap<String,String> map_data=new HashMap<>();

                                                    String picname=mAdapter.listItem.get(position).get("ItemImage").toString();
                                                    String datafilename = picname.substring(picname.lastIndexOf("/") + 1, picname.lastIndexOf("."));
                                                    map_data.put("orderId",orderId);
                                                    map_data.put("consignmentId",consignmentId);
                                                    map_data.put("deviceId",deviceId);
                                                    map_data.put("datafilename",datafilename);
                                                    Map<String, Object> paremetes = new HashMap<>();
                                                    paremetes.put("data", JSON.toJSONString(map_data));

                                                    ApiService.GetString(SelectEquipment.this, "deletePicture", paremetes, new RxStringCallback() {
                                                        boolean flag = false;

                                                        @Override
                                                        public void onNext(Object tag, String response) {

                                                            if (response.trim().equals("删除成功！")) {
                                                                String eqfilename = mAdapter.listItem.get(position).get("ItemImage").toString();
                                                                String datafile = eqfilename.substring(eqfilename.lastIndexOf("/") + 1, eqfilename.length());

                                                                FtpClientUpload.DeleteFile(orderId + "/" + consignmentId + "/" + deviceId + "/",datafile);

                                                                mAdapter.listItem.remove(position);
                                                                mAdapter.notifyDataSetChanged();
                                                                Toast.makeText(SelectEquipment.this,"删除成功",Toast.LENGTH_SHORT).show();

                                                            }
                                                        }

                                                        @Override
                                                        public void onError(Object tag, Throwable e) {
                                                            Toast.makeText(SelectEquipment.this, "删除失败" + e, Toast.LENGTH_SHORT).show();


                                                        }

                                                        @Override
                                                        public void onCancel(Object tag, Throwable e) {
                                                            Toast.makeText(SelectEquipment.this, "删除失败" + e, Toast.LENGTH_SHORT).show();

                                                        }
                                                    });


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                        }
                                    }).show();
                }
            });

            holder.modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final EditText et=new EditText(SelectEquipment.this);
                    et.setText(mAdapter.listItem.get(position).get("ItemText").toString());
                    new  AlertDialog.Builder(SelectEquipment.this)
                            .setMessage("请修改新的检验情况说明：")
                            .setView(et)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("确定",
                                    new  DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public  void  onClick(DialogInterface dialog, int  which)
                                        {

                                            String datatext=mAdapter.listItem.get(position).get("ItemText").toString();
                                            final String temimg = mAdapter.listItem.get(position).get("ItemImage").toString();
                                            final String tagtag = mAdapter.listItem.get(position).get("Tag").toString();

                                            if (mAdapter.listItem.get(position).get("Tag").toString().trim().equals("0")) {
                                                HashMap<String, Object> map = new HashMap<String, Object>();
                                                map.put("ItemImage", temimg);
                                                map.put("ItemText", et.getText());
                                                map.put("Tag", tagtag);
                                                mAdapter.listItem.remove(position);
                                                mAdapter.listItem.add(map);
                                                mAdapter.notifyDataSetChanged();
                                                Toast.makeText(SelectEquipment.this, "修改成功", Toast.LENGTH_SHORT).show();
                                            }else if (!et.getText().toString().equals(datatext)&&mAdapter.listItem.get(position).get("Tag").toString().trim().equals("1")){
                                                String filename = mAdapter.listItem.get(position).get("ItemImage").toString();
                                                String datafile = filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")) ;

                                                String picDescrip=et.getText().toString();
                                                HashMap<String,String> map_data=new HashMap<>();
                                                map_data.put("orderId",orderId);
                                                map_data.put("consignmentId",consignmentId);
                                                map_data.put("deviceId",deviceId);
                                                map_data.put("datafile",datafile);
                                                map_data.put("picDescrip",picDescrip);

                                                Map<String, Object> paremetes = new HashMap<>();
                                                paremetes.put("data", JSON.toJSONString(map_data));

                                                ApiService.GetString(SelectEquipment.this, "modifyDescription", paremetes, new RxStringCallback() {
                                                    boolean flag = false;

                                                    @Override
                                                    public void onNext(Object tag, String response) {
                                                        if (response.trim().equals("修改成功！")) {
                                                            HashMap<String, Object> map = new HashMap<String, Object>();
                                                            map.put("ItemImage", temimg);
                                                            map.put("ItemText", et.getText());
                                                            map.put("Tag", tagtag);
                                                            mAdapter.listItem.remove(position);
                                                            mAdapter.listItem.add(map);
                                                            mAdapter.notifyDataSetChanged();
                                                            Toast.makeText(SelectEquipment.this,"修改成功",Toast.LENGTH_SHORT).show();

                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Object tag, Throwable e) {
                                                        Toast.makeText(SelectEquipment.this, "修改失败" + e, Toast.LENGTH_SHORT).show();


                                                    }

                                                    @Override
                                                    public void onCancel(Object tag, Throwable e) {
                                                        Toast.makeText(SelectEquipment.this, "修改失败" + e, Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                            }


                                        }
                                    }).show();

                }
            });


            return convertView;
        }

    }
    /*存放控件*/
    public final class ViewHolder{
        public TextView title;
        public Button delete,modify;
        public ImageView pic;

    }



    public Bitmap loadingImageBitmap(String imagePath) {
        /**
         * 获取屏幕的宽与高
         */
        final int width = getWindowManager().getDefaultDisplay().getWidth();
        final int height = getWindowManager().getDefaultDisplay().getHeight();
        /**
         * 通过设置optios来只加载大图的尺寸
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            /**
             * 计算手机宽高与显示大图的宽高，然后确定缩放有比例
             */
            int widthRaio = (int) Math.ceil(options.outWidth/(float)width);
            int heightRaio = (int) Math.ceil(options.outHeight/(float)height);
            if (widthRaio>1&&heightRaio>1){
                if (widthRaio>heightRaio){
                    options.inSampleSize = widthRaio;
                }else {
                    options.inSampleSize = heightRaio;
                }
            }
            /**
             * 设置加载缩放后的图片
             */
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imagePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


}

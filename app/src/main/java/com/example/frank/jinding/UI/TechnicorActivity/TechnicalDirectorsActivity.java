package com.example.frank.jinding.UI.TechnicorActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frank.jinding.ExtraPermission.Permission;
import com.example.frank.jinding.R;
import com.example.frank.jinding.Service.ApiService;
import com.example.frank.jinding.UI.PersonAboutActivity.MessageInform;
import com.example.frank.jinding.UI.PersonAboutActivity.PersonInformation;
import com.example.frank.jinding.Utils.MenuMessage;
import com.example.frank.jinding.Utils.SnackbarUtils;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TechnicalDirectorsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private long mExitTime = 0;


    private PermissMyAdapter permissMyAdapter;
    private MyAdapterMessage myAdapter;
    private ListView lv_message;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_directors);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        init();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "您有新的消息，请注意查看！", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




    }


    private void init() {




        //绑定控件
        lv_message = (ListView) this.findViewById(R.id.lv_technical_messages);
        refreshLayout=(SwipeRefreshLayout)this.findViewById(R.id.technical_refresh);
        myAdapter=new MyAdapterMessage(this);
        lv_message.setAdapter(myAdapter);

        getMessageInfo();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                getMessageInfo();
            }
        });


        lv_message.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String menuname_item=myAdapter.listItem.get(arg2).get("ItemName").toString();

                if (menuname_item.contains("委托协议审核")){
                    Intent intent = new Intent(TechnicalDirectorsActivity.this, TDCheckOrder.class);
                    //intent.putExtra("userid",userid);
                    //startActivity(intent);
                    startActivityForResult(intent, 6666);
                }
                //Toast.makeText(TechnicalDirectorsActivity.this, "快捷事件处理方式正在研发中……" , Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initpermission() {
        permissMyAdapter = new PermissMyAdapter(TechnicalDirectorsActivity.this);
        getPermissions();
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.permissions_list, (ViewGroup) findViewById(R.id.permissions_list));
        final ListView lv_permissions = (ListView) layout.findViewById(R.id.permissions_listview);
        //final Spinner resultsp = (Spinner) layout.findViewById(R.id.spinner_check_opinion);
        lv_permissions.setAdapter(permissMyAdapter);
        lv_permissions.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                HashMap<String, Object> map = permissMyAdapter.listItem.get(arg2);
                Permission.excutePermissions(map.get("ItemText").toString(), TechnicalDirectorsActivity.this);

            }
        });


        new AlertDialog.Builder(TechnicalDirectorsActivity.this)
                .setTitle("额外的权限：")
                .setView(layout)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        }).show();

    }

    private void getPermissions() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, Object> paremetes = new HashMap<>();
                    paremetes.put("data", "pp");
                    ApiService.GetString(TechnicalDirectorsActivity.this, "extraPermissions", paremetes, new RxStringCallback() {


                        @Override
                        public void onNext(Object tag, String response) {


                            if (response.trim().split(":")[0].equals("true")) {
                                permissMyAdapter.listItem.clear();
                                permissMyAdapter.listItem = Permission.getPermissions(response.trim().split(":")[1]);
                                permissMyAdapter.notifyDataSetChanged();
                            } else if (response.trim().equals("没有额外权限！")) {
                                Toast.makeText(TechnicalDirectorsActivity.this, "没有额外权限", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Object tag, Throwable e) {
                            Toast.makeText(TechnicalDirectorsActivity.this, "获取失败" + e, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancel(Object tag, Throwable e) {
                            Toast.makeText(TechnicalDirectorsActivity.this, "获取失败" + e, Toast.LENGTH_SHORT).show();

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (System.currentTimeMillis() - mExitTime > 2000) {
            mExitTime = System.currentTimeMillis();
            SnackbarUtils.show(this, R.string.click2exit);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.technical_directors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_request2) {
           Intent intent=new Intent(TechnicalDirectorsActivity.this, TechnicalDirector.class);
           intent.putExtra("tag","0");
           //startActivity(intent);
            startActivityForResult(intent, 6666);

        }
        else if (id==R.id.td_order_check_history){
            Intent intent=new Intent(this,TDCheckOrder.class);
            intent.putExtra("requestCode",0x12);
            //startActivity(intent);
            startActivityForResult(intent, 6666);
        }
        else if (id == R.id.nav_history2) {
            Intent intent=new Intent(TechnicalDirectorsActivity.this, TechnicalDirector.class);
            intent.putExtra("tag","1");
           // startActivity(intent);
            startActivityForResult(intent, 6666);


        } else if (id == R.id.nav_messageinfo2) {
            Intent intent = new Intent(TechnicalDirectorsActivity.this, MessageInform.class);
            //intent.putExtra("userid",userid);
            startActivity(intent);
        } else if (id == R.id.nav_personinfo2) {
            Intent intent = new Intent(TechnicalDirectorsActivity.this, PersonInformation.class);
            //intent.putExtra("userid",userid);
            startActivity(intent);
        } else if (id == R.id.nav_exit2) {
            finish();
        } else if (id == R.id.extra_permission_technical) {
            initpermission();
        } else if (id == R.id.td_order_check) {
            Intent intent = new Intent(TechnicalDirectorsActivity.this, TDCheckOrder.class);
            //intent.putExtra("userid",userid);
            //startActivity(intent);
            startActivityForResult(intent, 6666);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==6666) {
            getMessageInfo();
        }
    }



    private void getMessageInfo(){
        refreshLayout.setRefreshing(false);

        new Thread( new Runnable() {
            @Override
            public void run() {
                try {


                    String data="查询待处理事务";
                    Map<String, Object> paremetes = new HashMap<>();
                    paremetes.put("data",data);
                    ApiService.GetString(TechnicalDirectorsActivity.this, "getMessageInfo", paremetes, new RxStringCallback() {
                        boolean flag = false;

                        @Override
                        public void onNext(Object tag, String response) {

                            if (!response.trim().equals("获取失败！")&&response.trim().length()>3) {

                                System.out.println("===========待处理信息=============="+response);
                                String data[]=response.split("##");
                                myAdapter.listItem.clear();
                                for (int i=0;i+1<data.length;i++){

                                    String dd[]=data[i].split("#");

                                    if (dd[2]!=null&&!dd[2].trim().equals("")&&Integer.parseInt(dd[2].trim())>0){

                                        if (MenuMessage.technicaldirectors.contains(dd[1].trim())){

                                            HashMap<String, Object> map=new HashMap<>();
                                            map.put("ItemName",dd[1].trim());
                                            map.put("ItemCount",dd[2].trim()+"");
                                            myAdapter.listItem.add(map);

                                        }
                                    }

                                }
                                myAdapter.notifyDataSetChanged();


                            }
                        }

                        @Override
                        public void onError(Object tag, Throwable e) {
                            Toast.makeText(TechnicalDirectorsActivity.this, "获取失败" + e, Toast.LENGTH_SHORT).show();
                            //refreshLayout.setRefreshing(false);

                        }

                        @Override
                        public void onCancel(Object tag, Throwable e) {
                            Toast.makeText(TechnicalDirectorsActivity.this, "获取失败" + e, Toast.LENGTH_SHORT).show();
                            //refreshLayout.setRefreshing(false);
                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }





    private class PermissMyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();

        /*构造函数*/
        public PermissMyAdapter(Context context) {
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
            final PermissViewHolder holder;
            //观察convertView随ListView滚动情况
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.extra_permissions, null);
                holder = new PermissViewHolder();
                /*得到各个控件的对象*/

                holder.title = (TextView) convertView.findViewById(R.id.textView_permissions);

                convertView.setTag(holder);//绑定ViewHolder对象
            } else {
                holder = (PermissViewHolder) convertView.getTag();//取出ViewHolder对象
            }

            holder.title.setText(listItem.get(position).get("ItemText").toString());

            return convertView;
        }

    }

    /*存放控件*/
    public final class PermissViewHolder {
        public TextView title;


    }





    private class MyAdapterMessage extends BaseAdapter {

        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局


        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();

        public MyAdapterMessage(Context context) {
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
            final ViewHolderMessage holder;

            //观察convertView随ListView滚动情况
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.message_tip,null);
                holder = new ViewHolderMessage();
                /*得到各个控件的对象*/

                holder.title = (TextView) convertView.findViewById(R.id.message_menu_name);
                holder.itemcount = (TextView) convertView.findViewById(R.id.message_item_count);

                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolderMessage) convertView.getTag();//取出ViewHolder对象
            }

            holder.title.setText(listItem.get(position).get("ItemName").toString());
            holder.itemcount.setText(listItem.get(position).get("ItemCount").toString());

            return convertView;
        }

    }
    /*存放控件*/
    public final class ViewHolderMessage{
        public TextView title,itemcount;

    }
}

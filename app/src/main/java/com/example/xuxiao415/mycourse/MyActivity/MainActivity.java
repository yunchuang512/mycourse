package com.example.xuxiao415.mycourse.MyActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.xuxiao415.mycourse.MyDataBase.DaoMaster;
import com.example.xuxiao415.mycourse.MyDataBase.DaoSession;
import com.example.xuxiao415.mycourse.MyDataBase.UserInfo;
import com.example.xuxiao415.mycourse.MyDataBase.UserInfoDao;
import com.example.xuxiao415.mycourse.MyView.ClearEditText;
import com.example.xuxiao415.mycourse.R;
import com.example.xuxiao415.mycourse.Utils.HttpRequest;
import com.example.xuxiao415.mycourse.Utils.MyUtils;
import com.example.xuxiao415.mycourse.Utils.SharedPreferencesUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.example.xuxiao415.mycourse.Utils.SharedPreferencesUtils.getParam;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.username)
    ClearEditText username;
    @BindView(R.id.userpassword)
    ClearEditText userpassword;
    @BindView(R.id.checkbox1)
    CheckBox checkbox1;
    @BindView(R.id.checkbox2)
    CheckBox checkbox2;

    private String uname = "";
    private String upassword = "";
    private Context mContext = this;

    private String[] User_Name = {"", "", "", "", ""}, User_Password = {"", "", "", "", ""};
    private int length;
    private ArrayAdapter<String> UserName = null;

    //数据库
    DaoMaster.DevOpenHelper devOpenHelper;
    DaoMaster daoMaster;
    DaoSession daoSession;
    UserInfoDao userInfoDao;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(mContext, "用户名密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                    //登陆成功后用户信息存储到数据库
                    try {

                        if (userInfoDao.queryBuilder().where(UserInfoDao.Properties.UserName.eq(uname)).list().isEmpty()){
                            SharedPreferencesUtils.setParam(mContext, "DB_flag", true);
                            UserInfo userInfo = new UserInfo(null, uname, upassword);
                            userInfoDao.insert(userInfo);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loginStatusJudgment();

        InitDB_Operation();

        if ((Boolean) SharedPreferencesUtils.getParam(mContext, "DB_flag", false)) {
            List<UserInfo> results = userInfoDao.loadAll();
            int i = 0;
            for (UserInfo result : results) {
                User_Name[i] = result.getUserName();
                User_Password[i] = result.getUserPassword();
                i++;
            }
            length = i;
            UserName = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, User_Name);
        } else
            Log.i("----MyCurriculum----", "表不存在");


        InitView();
    }
    private void loginStatusJudgment(){
        boolean isLogin = (boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false);
        if (isLogin) {
            Intent intent = new Intent(MainActivity.this, CurrriculumActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //与数据库操作相关的对象的初始化
    private void InitDB_Operation(){
        devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "CurriculumTable.db", null);
        daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        userInfoDao = daoSession.getUserInfoDao();
    }

    private void InitView() {

        uname = (String) getParam(mContext, "USER_NAME", "");
        upassword = (String) getParam(mContext, "PASSWORD", "");

        if ((Boolean) getParam(mContext, "remember", false)) {
            Log.i("MyCurriculum", "------------记住密码------------");
            username.setText(uname);
            userpassword.setText(upassword);
            checkbox1.setChecked(true);
        }
        if (((Boolean) getParam(mContext, "autologin", false)) && ((boolean) SharedPreferencesUtils.getParam(mContext, "isLogin", false))) {
            Log.i("MyCurriculum", "------------自动登录------------");
            Intent intent = new Intent(MainActivity.this, CurrriculumActivity.class);
            startActivity(intent);
            finish();
        }

        username.setAdapter(UserName);

        //Item的监听事件
        username.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String listItem = (String) parent.getItemAtPosition(position);
                Log.i("MyCurriculum", "---------" + listItem + "---------");
                userpassword.setText(User_Password[MyUtils.stringArray_match(User_Name, length, listItem)]);
            }
        });
    }

    //登录函数，请求数据
    public void loginfuntion() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String result = HttpRequest.sendGet("http://202.113.5.137/stuslls/login/login!login", "loginBean.username=" + uname + "&loginBean.password=" + upassword + "&Submit=%E7%99%BB+%E5%BD%95", mContext);

                if (result.contains("用户名密码错误")) {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 2;
                    mHandler.sendMessage(message);
                    SharedPreferencesUtils.setParam(mContext, "isLogin", true);
                    Intent intent = new Intent(MainActivity.this, CurrriculumActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            }
        });
        thread.start();
    }

    //吸收启动界面的返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(Intent.ACTION_MAIN);  //主启动，不期望接收数据

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       //新的activity栈中开启，或者已经存在就调到栈前

            i.addCategory(Intent.CATEGORY_HOME);            //添加种类，为设备首次启动显示的页面

            startActivity(i);
        }
        return super.onKeyDown(keyCode, event);
    }

    //登录按钮的点击事件
    @OnClick(R.id.loginbutton)
    public void onClick() {
        uname = username.getText().toString();
        upassword = userpassword.getText().toString();
        if (uname.equals("")) {
            Toast.makeText(mContext, "学号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (upassword.equals("")) {
            Toast.makeText(mContext, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println("用户名：" + uname);
        System.out.println("密码：" + upassword);
        SharedPreferencesUtils.setParam(mContext, "USER_NAME", uname);
        SharedPreferencesUtils.setParam(mContext, "PASSWORD", upassword);
        loginfuntion();
    }

    //复选框的点击事件
    @OnCheckedChanged({R.id.checkbox1, R.id.checkbox2})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkbox1:
                //记住密码复选框检查
                if (isChecked) {
                    SharedPreferencesUtils.setParam(mContext, "remember", true);
                } else
                    SharedPreferencesUtils.setParam(mContext, "remember", false);
                break;
            case R.id.checkbox2:
                //自动登录复选框检查
                if (isChecked) {
                    checkbox1.setChecked(true);
                    SharedPreferencesUtils.setParam(mContext, "autologin", true);
                } else
                    SharedPreferencesUtils.setParam(mContext, "autologin", false);
                break;
        }
    }
}

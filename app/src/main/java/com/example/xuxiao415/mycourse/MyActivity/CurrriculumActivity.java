package com.example.xuxiao415.mycourse.MyActivity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xuxiao415.mycourse.MyDataBase.DaoMaster;
import com.example.xuxiao415.mycourse.MyDataBase.DaoSession;
import com.example.xuxiao415.mycourse.MyDataBase.MyCurriculumTable;
import com.example.xuxiao415.mycourse.MyDataBase.MyCurriculumTableDao;
import com.example.xuxiao415.mycourse.MyDataClass.CurriculumEntity;
import com.example.xuxiao415.mycourse.R;
import com.example.xuxiao415.mycourse.Utils.HttpRequest;
import com.example.xuxiao415.mycourse.Utils.MyUtils;
import com.example.xuxiao415.mycourse.Utils.SharedPreferencesUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuxiao415 on 2016/9/6.
 * 课程表信息主页
 */

/**
 * 课程表信息页
 */
public class CurrriculumActivity extends Activity {

    //用户名
    //用于检测数据库中是否有该用户的课程表数据
    private String studentNumber;
    //登录界面获取的cookie
    private String mCookie = "";
    //网络数据解析后的文档
    private Document doc;
    //存储课程表数据的List
    private List<CurriculumEntity> curriculum;
    //上下文环境变量
    private Context mContext = this;
    //课程表主页右上方的菜单按钮
    private ImageView moreButton;
    //存储课程标签的布局的List，大小为7
    private List<FrameLayout> layouts;
    //课程标签背景数组
    private int[] backgrounds = new int[15];
    //与数据库操作相关的对象
    DaoMaster.DevOpenHelper devOpenHelper;
    DaoMaster daoMaster;
    DaoSession daoSession;
    MyCurriculumTableDao mCurriculumTableDao;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    System.out.println("################" + "start");
                    //准备数据
                    prepareCourseData();
                    //创建课程表
                    createTableView();
                    break;
                case 2:
                    Log.i("MyCurriculum", "------------读取数据库------------");
                    createTableView();
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
        setContentView(R.layout.activity_table_view);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.CurriculumBackgrounds);
        for (int i = 0; i < typedArray.length(); i++)
            backgrounds[i] = typedArray.getResourceId(i, 0);
        InitView();

        devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "CurriculumTable.db", null);
        daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        mCurriculumTableDao = daoSession.getMyCurriculumTableDao();

        studentNumber = (String) SharedPreferencesUtils.getParam(mContext, "USER_NAME", "");

        boolean db_flag = !(mCurriculumTableDao.queryBuilder().where(MyCurriculumTableDao.Properties.StudentNumber.eq(studentNumber)).list().isEmpty());

        //课表初始化
        curriculum = new ArrayList<>();

        if (db_flag) {
            getMycurriculumFromDB();
        } else {
            //获得cookie
            mCookie = (String) SharedPreferencesUtils.getParam(mContext, "cookie", "");
            if (mCookie == null)
                Log.i("MyCurriculum", "------------cookie获取失败！！！------------");
            else
                Log.i("MyCurriculum", "------------cookie获取成功！！！------------");
            //获取课程表
            getMyCourses();
        }
    }

    public void InitView() {

        moreButton = (ImageView) findViewById(R.id.morebutton);

        FrameLayout layout1 = (FrameLayout) findViewById(R.id.framelayout1);
        FrameLayout layout2 = (FrameLayout) findViewById(R.id.framelayout2);
        FrameLayout layout3 = (FrameLayout) findViewById(R.id.framelayout3);
        FrameLayout layout4 = (FrameLayout) findViewById(R.id.framelayout4);
        FrameLayout layout5 = (FrameLayout) findViewById(R.id.framelayout5);
        FrameLayout layout6 = (FrameLayout) findViewById(R.id.framelayout6);
        FrameLayout layout7 = (FrameLayout) findViewById(R.id.framelayout7);

        layouts = new ArrayList<>();

        layouts.add(layout1);
        layouts.add(layout2);
        layouts.add(layout3);
        layouts.add(layout4);
        layouts.add(layout5);
        layouts.add(layout6);
        layouts.add(layout7);

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });

    }

    //从数据库中获取课程表数据
    public void getMycurriculumFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MyCurriculumTable> results = mCurriculumTableDao.queryBuilder().where(MyCurriculumTableDao.Properties.StudentNumber.eq(studentNumber)).list();
                for (MyCurriculumTable result : results) {
                    CurriculumEntity entity = new CurriculumEntity();
                    entity.setId(result.getC_id());
                    entity.setStudentNumber(result.getStudentNumber());
                    entity.setClassNumber(result.getClassNumber());
                    entity.setcName(result.getCName());
                    entity.setcNumber(result.getCNumber());
                    entity.setcTime(result.getCTime());
                    entity.setcType(result.getCType());
                    entity.settName(result.getTName());
                    entity.setLocation(result.getLocation());
                    entity.setPeriod(result.getPeriod());
                    entity.setWeekday(result.getWeekday());
                    curriculum.add(entity);
                }
                Message message = new Message();
                message.what = 2;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    //从网络上获取课程表数据
    public void getMyCourses() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String html = HttpRequest.sendGet("http://202.113.5.137/stuslls/course/course!selectedCourse", "", mCookie);
                System.out.println(html);
                doc = Jsoup.parse(html);
                Log.i("----MyCurriculum-----", doc.toString());
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    //准备课程表数据
    private void prepareCourseData() {
        Log.i("MyCurriculum", "------------Prepare course data------------");

        Element table = doc.getElementsByClass("defaulettable").get(0);
        Elements tr = table.getElementsByTag("tr");
        int id = 0;
        for (int i = 1; i < tr.size(); i++) {
            Elements tds = tr.get(i).getElementsByTag("td");
            String stno = tds.get(1).text();
            String cuno = tds.get(2).text();
            String cname = tds.get(3).text();
            String ctype = tds.get(4).text();
            String tname = tds.get(5).text();
            String time = tds.get(6).text();
            System.out.println(">>>>>>>>>>>>>>>>>>>>" + time);
            if (time.equals("")) {
                CurriculumEntity entity = new CurriculumEntity();
                entity.setcName(cname);
                entity.setcType(ctype);
                entity.setcNumber(cuno);
                entity.setClassNumber(stno);
                entity.settName(tname);
                entity.setId(id);
                entity.setStudentNumber(studentNumber);
                curriculum.add(entity);

            } else {
                String[] times = time.split("] ");
                for (String temp_time : times) {
                    System.out.println(">>>>" + temp_time);
                    String cdate = "", cweek = "", ctime = "", cplace = "";
                    String[] timearray = temp_time.split(",");
                    if (timearray.length == 3) {
                        cdate = temp_time.split(",")[0];
                        cweek = temp_time.split(",")[1];
                        String a = temp_time.split(",")[2];
                        ctime = a.split("\\[")[0];
                        cplace = a.split("\\[")[1].replace("]", "");
                    } else if (timearray.length == 4) {
                        cdate = temp_time.split(",")[0];
                        cweek = temp_time.split(",")[2];
                        String a = temp_time.split(",")[3];
                        ctime = a.split("\\[")[0];
                        cplace = a.split("\\[")[1].replace("]", "");
                    }
                    System.out.println(">>>>>>>>>>>>>>>>>>>>" + cdate + cweek + ctime + cplace);
                    CurriculumEntity entity = new CurriculumEntity();
                    entity.setPeriod(cdate);
                    entity.setcName(cname);
                    entity.setLocation(cplace);
                    entity.setWeekday(cweek);
                    entity.setcType(ctype);
                    entity.setcTime(ctime);
                    entity.setcNumber(cuno);
                    entity.setClassNumber(stno);
                    entity.settName(tname);
                    entity.setId(id);
                    entity.setStudentNumber(studentNumber);
                    curriculum.add(entity);

                }
            }
            id++;
        }
        //开启线程将课程信息存入数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (CurriculumEntity curriculumEntity : curriculum) {
                        if (curriculumEntity.getcTime().equals("")) {
                            MyCurriculumTable myCurriculumTable = new MyCurriculumTable(null, curriculumEntity.getId(), curriculumEntity.getStudentNumber(), curriculumEntity.getClassNumber(),
                                    curriculumEntity.getcNumber(), curriculumEntity.getcName(), curriculumEntity.getcType(), curriculumEntity.gettName()
                                    , null, null, null, null);
                            mCurriculumTableDao.insert(myCurriculumTable);
                        } else {
                            MyCurriculumTable myCurriculumTable = new MyCurriculumTable(null, curriculumEntity.getId(), curriculumEntity.getStudentNumber(), curriculumEntity.getClassNumber(),
                                    curriculumEntity.getcNumber(), curriculumEntity.getcName(), curriculumEntity.getcType(), curriculumEntity.gettName(),
                                    curriculumEntity.getPeriod(), curriculumEntity.getcTime(), curriculumEntity.getWeekday(), curriculumEntity.getLocation());
                            mCurriculumTableDao.insert(myCurriculumTable);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void createTableView() {
        for (int i = 0; i < curriculum.size(); i++) {

            CurriculumEntity course = curriculum.get(i);
            if (course.getWeekday() == null)
                continue;
            System.out.println(curriculum.get(i).getcTime() + ">>" + curriculum.get(i).getWeekday() + "<<");
            int margintop = Integer.parseInt(course.getcTime().split("-")[0].split("第")[1]);
            System.out.println(margintop + " ");
            int viewheight = Integer.parseInt(course.getcTime().split("-")[1].split("节")[0]) - Integer.parseInt(course.getcTime().split("-")[0].split("第")[1]);

            int intlayout = 0;
            System.out.println(margintop + " " + viewheight + " " + ">>>>>>>>>>>>>>>>>>>>>>");

            try {
                intlayout = Integer.parseInt(course.getWeekday().split("周")[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            System.out.println(margintop + " " + viewheight + " " + course.getId());

            addClassView(margintop, viewheight, intlayout, course.getcName() + "@" + course.getLocation(), course.getId());
        }

    }

    //往课程表中添加课程信息
    public void addClassView(final int margintop, final int viewheight, final int intlayout, final String sclass, final int intcolor) {
        final TextView textview = new TextView(mContext);
        textview.setHeight(MyUtils.dip2px(mContext, 60 * (viewheight + 1)));
        textview.setGravity(Gravity.CENTER);
        textview.setBackgroundResource(backgrounds[intcolor]);
        textview.setText(sclass);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, sclass, Toast.LENGTH_SHORT).show();
            }
        });
        FrameLayout.LayoutParams tparams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tparams.setMargins(0, MyUtils.dip2px(mContext, 60 * (margintop - 1)), 0, 0);
        layouts.get(intlayout - 1).addView(textview, tparams);
        System.out.println(sclass + "success");
    }

    //显示popupwindow
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void showPopupWindow(View view) {

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.activity_popupwindow, null);
        Button unsubscribeButton = (Button) contentView.findViewById(R.id.unsubscribe);
        moreButton = (ImageView) contentView.findViewById(R.id.morebutton);
        final PopupWindow popupwindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupwindow.setTouchable(true);
        popupwindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        popupwindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效
        popupwindow.showAsDropDown(view);

        if (unsubscribeButton != null) {
            unsubscribeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtils.setParam(mContext, "isLogin", false);
                    SharedPreferencesUtils.setParam(mContext, "autologin", false);
                    Intent unsubscribeIntent = new Intent(CurrriculumActivity.this, MainActivity.class);
                    unsubscribeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(unsubscribeIntent);
                }
            });
        } else {
            System.out.println(">>>>>");
        }
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
}

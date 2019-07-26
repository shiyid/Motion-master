package com.health.runing.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.SPUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.health.runing.MyApplication;
import com.health.runing.R;
import com.health.runing.commmon.utils.Conn;
import com.health.runing.commmon.utils.MySp;
import com.health.runing.commmon.utils.Utils;
import com.health.runing.db.DataManager;
import com.health.runing.db.RealmHelper;
import com.health.runing.ui.BaseActivity;
import com.health.runing.ui.fragment.FastLoginFragment;
import com.health.runing.ui.fragment.PsdLoginFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 包名：com.james.motion.ui.activity
 * 项目名称：Motion
 * 作者：james
 * 创建时间：2019-02-25 16:56
 * 描述：描述信息
 * 版本：V1.0
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.btLogin)
    Button btLogin;
    @BindView(R.id.btReg)
    Button btReg;
//    @BindView(R.id.wcLogin)
//    ImageButton wcLogin;
//    @BindView(R.id.qqLogin)
//    ImageButton qqLogin;

    /**
     * 上次点击返回键的时间
     */
    private long lastBackPressed;

    //上次点击返回键的时间
    public static final int QUIT_INTERVAL = 2500;

    private final String[] mTitles = {"普通登录", "快速登录"};

    private ArrayList<Fragment> mFragments = new ArrayList<>();

    private boolean isPsd = true;//是否是密码登录

    private PsdLoginFragment psdLoginFragment = new PsdLoginFragment();
    private FastLoginFragment fastLoginFragment = new FastLoginFragment();

    private DataManager dataManager = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        dataManager = new DataManager(new RealmHelper());

        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);

        mFragments.add(psdLoginFragment);
        mFragments.add(fastLoginFragment);

        slidingTabLayout.setViewPager(vp, mTitles, this, mFragments);

        isPsd = true;
    }

    @Override
    public void initListener() {
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                isPsd = i == 0;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @OnClick({R.id.container, R.id.btLogin, R.id.btReg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.container:
                hideSoftKeyBoard();
                break;
            case R.id.btLogin:
                if (isPsd) {
                    psdLoginFragment.checkAccount(this::login);
                } else {
                    fastLoginFragment.checkAccount(this::login);
                }
                break;
            case R.id.btReg:
                startActivity(new Intent(LoginActivity.this, RegistActivity.class));
                break;
//            case R.id.qqLogin:
//                showToast("功能开发中....");
//                break;
//            case R.id.wcLogin:
//                showToast("功能开发中....");
//                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    public void login(String account, String psd) {
        btLogin.setEnabled(false);
        showLoadingView();
        new Handler().postDelayed(() -> {
            dismissLoadingView();
            btLogin.setEnabled(true);
//            if (isPsd) {
//                if (dataManager.checkAccount(account, psd))
//                    loginSuccess(account, psd);
//                else
//                    showToast("账号或密码错误!");
//            } else {
//                if (dataManager.checkAccount(account))
                    loginSuccess(account, "");
//                else
//                    showToast("账号不存在!");
//            }
        }, Conn.Delayed);
    }

    private void loginSuccess(String account, String psd) {
        SPUtils.getInstance().put(MySp.ISLOGIN, true);

        SPUtils.getInstance().put(MySp.USERID, account.substring(8));

        SPUtils.getInstance().put(MySp.PHONE, account);
        SPUtils.getInstance().put(MySp.PASSWORD, psd);

        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        Utils.showToast(LoginActivity.this, "恭喜您,登录成功...");

        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { // 表示按返回键 时的操作
                long backPressed = System.currentTimeMillis();
                if (backPressed - lastBackPressed > QUIT_INTERVAL) {
                    lastBackPressed = backPressed;
                    showToast("再按一次退出");
                } else {
                    moveTaskToBack(false);
                    MyApplication.closeApp(this);
                    finish();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        if (null != dataManager)
            dataManager.closeRealm();
        super.onDestroy();
    }
}

package com.nebulium.min;

import com.unity3d.player.*;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ourpalm.android.account.Ourpalm_Account_Interface;
import ourpalm.android.account.net.Ourpalm_Account_Net;
import ourpalm.android.callback.Ourpalm_CallBackListener;
import ourpalm.android.callback.Ourpalm_PaymentCallBack;
import ourpalm.android.info.GameInfo;
import ourpalm.android.opservice.Ourpalm_OpService_Entry;
import ourpalm.android.pay.Ourpalm_Entry;
import ourpalm.tools.android.logs.Logs;

public class UnityPlayerActivity extends Activity
{
	protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code

    public static final String LOG_TAG_OURPALM = "Ourpalm";

	// Setup activity layout
	@Override protected void onCreate (Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

		mUnityPlayer = new UnityPlayer(this);
		setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();

		// 初始化SDK
		Ourpalm_Entry.getInstance(this).Ourpalm_Init(GameInfo.GameType_Online, "", "",
				new Ourpalm_CallBackListener() {
					@Override
					public void Ourpalm_LogoutSuccess() {
// TODO Auto-generated method stub
// 当前渠道带有注销功能的话由游戏实现，如返回到登录界面。
// 单机游戏不用实现
                        Log.i(LOG_TAG_OURPALM, "logout success");
						UnityPlayer.UnitySendMessage("Ourpalm", "LogoutSuccess", "");
					}

					@Override
					public void Ourpalm_LogoutFail(int code) {
// TODO Auto-generated method stub
// 当前渠道带有注销功能的话由游戏实现
// 单机游戏不用实现
                        Log.i(LOG_TAG_OURPALM, "logout fail, code:" + code);
					}

					@Override
					public void Ourpalm_LoginSuccess(String tokenId, String data) {
// TODO Auto-generated method stub
// 登录成功回调，网络游戏必须实现
// 单机游戏不用实现
                        Log.i(LOG_TAG_OURPALM, "login success, token:" + tokenId + ", data:" + data);
                        UnityPlayer.UnitySendMessage("Ourpalm", "LoginSuccess", tokenId + "," + data);
					}

					@Override
					public void Ourpalm_LoginFail(int code) {
// TODO Auto-generated method stub
// 登录失败回调，网络游戏必须实现
// 单机游戏不用实现
                        Log.i(LOG_TAG_OURPALM, "login fail, code:" + code);
                        UnityPlayer.UnitySendMessage("Ourpalm", "LoginFail", String.valueOf(code));
					}

					@Override
					public void Ourpalm_InitSuccess() {
// TODO Auto-generated method stub
// 待SDK的初始化完成后，游戏开始实现游戏本身的初始化
// 初始化游戏
//						initGame();
                        Log.i(LOG_TAG_OURPALM, "init success");
                        UnityPlayer.UnitySendMessage("Ourpalm", "InitSuccess", "");
					}

					@Override
					public void Ourpalm_InitFail(int code) {
// TODO Auto-generated method stub
// SDK初始化失败的回调，游戏可以在这里实现自己的逻辑处理，可以只提示用户后退出游戏，也可以继续游戏，但SDK的某些功能可能会受到影响
                        Log.i(LOG_TAG_OURPALM, "init fail:" + code);
                        UnityPlayer.UnitySendMessage("Ourpalm", "InitFail", String.valueOf(code));
					}

					@Override
					public void Ourpalm_ExitGame() {
// TODO Auto-generated method stub
// 游戏使用掌趣SDK退出接口时必须实现此回调，在此处实现退出游戏的功能。
						UnityPlayerActivity.this.finish();
// android.os.Process.killProcess(android.os.Process.myPid());
					}

					@Override
					public void Ourpalm_SwitchingAccount(boolean Success, String tokenId, String userInfo) {
// TODO Auto-generated method stub
// 当前渠道带有切换账号功能时，游戏需要实现此接口逻辑进行游戏登录。切换账号分2种情况：
//一是调用SDK的切换账号接口实现账号的切换，此类型的切换账号按钮是由游戏去实现，建议放到登录界面，切换账号成功后，登录数据会在此接口返回，游戏可执行游戏的登录流程，类似登录成功接口的回调。
//二是渠道SDK的悬浮框或者用户中心页面带有切换账号功能，如用户点击切换账号并登录成功后，登录数据会在此接口返回，但如果当前游戏场景处于游戏中，游戏正常应该先返回到登录界面，然后再通过SDK切换账号成功返回的数据执行游戏的登录流程（游戏能在游戏中直接实现账号切换，人物改变的除外，不需返回登录界面）。

// Success=true表示切换成功，Success=false表示切换失败，tokenId和userInfo同登录成功接口
// 单机游戏不用实现
						if (Success)
							UnityPlayer.UnitySendMessage("Ourpalm", "SwitchAccount", tokenId + "," + userInfo);
					}

					@Override
					public void Ourpalm_PayInventorySuccess(String ssid, String pbid) {
// TODO Auto-generated method stub
// 单机游戏补单接口，网游不用实现
					}
				});
	}

    public void OurpalmLogin()
    {
        Ourpalm_Entry.getInstance(UnityPlayerActivity.this).Ourpalm_Login();
    }

    public void OurpalmLogout()
    {
        Ourpalm_Entry.getInstance(UnityPlayerActivity.this).Ourpalm_Logout();
    }

	/**
	 * 账号切换
	 */
	public void OurpalmSwitchAccount()
	{
		Ourpalm_Entry.getInstance(UnityPlayerActivity.this).Ourpalm_SwitchAccount();
	}

	/**
	 * 获取当前渠道游戏包是否要调用“用户中心”，“切换账号”，“登录注销”等接口
	 */
	public String OurpalmGetEnableInterface()
	{
		return Ourpalm_Entry.getInstance(UnityPlayerActivity.this).Ourpalm_GetEnableInterface();
	}

	/**
	 * 进入用户中心接口，如果是用官网登录的则进入官网的用户中心
	 */
	public void OurpalmGoCenter()
	{
		Ourpalm_Entry.getInstance(UnityPlayerActivity.this).Ourpalm_GoCenter();
	}

	/**
	 * 调用第三方SDK退出提示页面，如果第三方SDK无退出提示页面则调用掌趣提示页面
	 * @param usetip
	 * 是否使用掌趣SDK的退出提示界面
	 */
	public void OurpalmExitGame(boolean usetip)
	{
		Ourpalm_Entry.getInstance(this).Ourpalm_ExitGame(usetip);
	}

	/**
	 * 进入客服反馈页面
	 */
	public  void OurpalmEnterServiceQuestion()
	{
		Ourpalm_OpService_Entry.getInstance().EnterServiceQuestion();
	}

	/**
     * 设置游戏角色信息
     * @param type
     * 1：角色注册，2：角色登录
     * @param gameservername
     * 当前角色所属服务器名称
     * @param gameserverid
     * 当前角色所属服务器ID
     * @param rolename
     * 当前角色名字
     * @param roleid
     * 当前角色ID
     * @param rolelv
     * 当前角色等级，无等级可传""
     * @param roleviplv
     * 当前角色VIP等级，无VIP等级可传""
     *
     */
    public void OurpalmSetGameInfo(int type, String gameservername,String gameserverid, String rolename, String roleid, String rolelv,String roleviplv)
    {
        Ourpalm_Entry.getInstance(UnityPlayerActivity.this).Ourpalm_SetGameInfo(type, gameservername, gameserverid, rolename, roleid, rolelv, roleviplv);
    }

    /**
     *
     * @param propId
     * 游戏自定义的商品ID
     * @param chargeCash
     * 商品价格
     * @param currencyType
     * 货币类型(1人民币2美元3日元4港币5英镑6新加坡币7越南盾8台币9韩元10泰铢)
     * @param propName
     * 商品名称
     * @param propCount
     * 商品数量
     * @param propDes
     * 商品描述
     * @param Gameurl
     * 掌趣计费中心回调游戏发货地址
     * @param ExtendParams
     * 扩展参数
     * @param rolelv
     * 角色等级
     * @param roleviplv
     * 角色VIP等级
     */
    public void OurpalmPay(final String propId, final String chargeCash,
                            final String currencyType, final String propName, final String propCount,
                            final String propDes,final String Gameurl,final String ExtendParams,
                            final String rolelv, final String roleviplv)
    {
        Ourpalm_Entry.getInstance(UnityPlayerActivity.this).Ourpalm_Pay(propId, chargeCash, currencyType, propName, propCount, propDes, Gameurl, ExtendParams,
                new Ourpalm_PaymentCallBack() {
                    @Override
                    public void Ourpalm_PaymentSuccess(int code, String ssid,String pbid) {
                        // TODO Auto-generated method stub
                        Log.i(LOG_TAG_OURPALM, "Ourpalm_PaymentSuccess ssid == " + ssid + " , pbid = " + pbid);
						UnityPlayer.UnitySendMessage("Ourpalm", "PaymentSuccess", String.format("%d,%s,%s", code, ssid, pbid));
                    }

                    @Override
                    public void Ourpalm_PaymentFail(int code, String ssid, String  pbid) {
                        // TODO Auto-generated method stub
                        Log.i(LOG_TAG_OURPALM, "Ourpalm_PaymentFail code = " + code + "ssid  == " + ssid + " , pbid = "+ pbid);
						UnityPlayer.UnitySendMessage("Ourpalm", "PaymentFail", String.format("%d,%s,%s", code, ssid, pbid));
                    }

                    @Override
                    public void Ourpalm_OrderSuccess(int code, String ssid, String pbid) {
                        // TODO Auto-generated method stub
                    }
                }, rolelv, roleviplv);
    }

    // Quit Unity
	@Override protected void onDestroy ()
	{
		mUnityPlayer.quit();
		super.onDestroy();

        Ourpalm_Entry.getInstance(this).Ourpalm_onDestroy();
    }

	// Pause Unity
	@Override protected void onPause()
	{
		super.onPause();
		mUnityPlayer.pause();

        Ourpalm_Entry.getInstance(this).Ourpalm_onPause();
	}

	// Resume Unity
	@Override protected void onResume()
	{
		super.onResume();
		mUnityPlayer.resume();

        Ourpalm_Entry.getInstance(this).Ourpalm_onResume();
	}

    @Override protected void onStart()
    {
        super.onStart();

        Ourpalm_Entry.getInstance(this).Ourpalm_onStart();
    }

    @Override protected void onRestart()
    {
        super.onRestart();

        Ourpalm_Entry.getInstance(this).Ourpalm_onRestart();
    }

	@Override protected  void onStop()
	{
		super.onStop();

		Ourpalm_Entry.getInstance(this).Ourpalm_onStop();
	}

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Ourpalm_Entry.getInstance(this).Ourpalm_onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        Ourpalm_Entry.getInstance(this).Ourpalm_onNewIntent(intent);
    }

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);

        Ourpalm_Entry.getInstance(this).Ourpalm_onConfigurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}

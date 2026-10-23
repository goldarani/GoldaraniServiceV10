package ir.goldarani.service;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.text.InputType;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainActivity extends Activity {
    private final int NAVY = Color.rgb(7,22,41);
    private final int PANEL = Color.rgb(15,39,62);
    private final int PANEL2 = Color.rgb(20,51,78);
    private final int GOLD = Color.rgb(246,190,79);
    private final int WHITE = Color.rgb(240,246,255);
    private final int MUTED = Color.rgb(165,187,209);
    private final int BLUE = Color.rgb(20,127,246);
    private final int TEAL = Color.rgb(0,165,157);
    private final int ORANGE = Color.rgb(255,139,50);
    private final int PURPLE = Color.rgb(135,75,220);
    private Db db;
    private String userName = "", userPhone = "";
    private boolean dark = true;
    private float fontScale = 1.0f;
    private LinearLayout root, content;
    private String page = "home";

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        db = new Db(this);
        android.content.SharedPreferences p = getSharedPreferences("profile", MODE_PRIVATE);
        userName = p.getString("name", "");
        userPhone = p.getString("phone", "");
        dark = p.getBoolean("dark", true);
        fontScale = p.getFloat("font", 1.0f);
        if (userName.trim().isEmpty()) showLogin(); else showHome();
    }

    private int bg() { return dark ? NAVY : Color.rgb(239,244,250); }
    private int panel() { return dark ? PANEL : Color.WHITE; }
    private int txt() { return dark ? WHITE : Color.rgb(20,37,55); }
    private int muted() { return dark ? MUTED : Color.rgb(90,108,126); }

    private GradientDrawable shape(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius * getResources().getDisplayMetrics().density);
        if (stroke != 0) d.setStroke((int)(1 * getResources().getDisplayMetrics().density), stroke);
        return d;
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextColor(color); t.setTextSize(size * fontScale);
        t.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        t.setPadding(dp(2), dp(3), dp(2), dp(3));
        return t;
    }

    private int dp(float x) { return (int)(x * getResources().getDisplayMetrics().density + 0.5f); }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l;
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }

    private void setup(String title) {
        root = column(); root.setBackgroundColor(bg());
        ScrollView sc = new ScrollView(this); sc.setFillViewport(true);
        content = column(); content.setPadding(dp(18), dp(12), dp(18), dp(20));
        sc.addView(content);
        root.addView(sc, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout bar = row(); bar.setPadding(dp(16),dp(10),dp(16),dp(10)); bar.setBackgroundColor(dark ? Color.rgb(5,17,32) : Color.rgb(225,234,243));
        TextView back = text("‹", 28, GOLD, true); back.setGravity(Gravity.CENTER); bar.addView(back,new LinearLayout.LayoutParams(dp(40),dp(42)));
        back.setOnClickListener(v -> showHome());
        TextView titleView = text(title, 18, txt(), true); titleView.setGravity(Gravity.CENTER);
        bar.addView(titleView,new LinearLayout.LayoutParams(0,dp(42),1));
        TextView mark = text("✦", 22, GOLD, true); mark.setGravity(Gravity.CENTER); bar.addView(mark,new LinearLayout.LayoutParams(dp(36),dp(42)));
        root.addView(bar);
        setContentView(root);
    }

    private TextView button(String label, int color, Runnable action) {
        TextView b = text(label, 15, Color.WHITE, true); b.setGravity(Gravity.CENTER);
        b.setPadding(dp(12),dp(13),dp(12),dp(13)); b.setBackground(shape(color,16,0));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,dp(6),0,dp(6));
        b.setOnClickListener(v -> action.run()); return b;
    }

    private void addButton(String label, int color, Runnable action) {
        TextView b = button(label,color,action); content.addView(b);
    }

    private void card(LinearLayout parent, View child) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,dp(6),0,dp(6));
        child.setPadding(dp(14),dp(12),dp(14),dp(12)); child.setBackground(shape(panel(),18,dark?Color.rgb(31,66,96):Color.rgb(215,226,238)));
        parent.addView(child,lp);
    }

    private EditText edit(String hint, String value, int inputType) {
        EditText e = new EditText(this); e.setSingleLine(true); e.setText(value);
        e.setHint(hint); e.setTextColor(txt()); e.setHintTextColor(muted()); e.setTextSize(15*fontScale);
        e.setInputType(inputType); e.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        e.setPadding(dp(14),dp(9),dp(14),dp(9)); e.setBackground(shape(panel(),14,dark?Color.rgb(52,83,111):Color.rgb(190,205,220)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,dp(6),0,dp(8));
        return e;
    }

    private void showLogin() {
        setup("ورود به سرویس‌یار");
        LinearLayout hero = column(); hero.setGravity(Gravity.CENTER); hero.setPadding(dp(10),dp(22),dp(10),dp(18));
        TextView icon = text("⇅\n▣", 34, GOLD, true); icon.setGravity(Gravity.CENTER); icon.setBackground(shape(PANEL2,100,GOLD));
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(100),dp(100)); ip.gravity=Gravity.CENTER; hero.addView(icon,ip);
        TextView title = text("سرویس‌یار گلدرانی",25,GOLD,true); title.setGravity(Gravity.CENTER); hero.addView(title);
        TextView sub = text("مدیریت آسان، خدمات مطمئن",14,muted(),false); sub.setGravity(Gravity.CENTER); hero.addView(sub);
        content.addView(hero);
        TextView prompt = text("برای ورود، اطلاعات خود را وارد کنید",15,txt(),true); prompt.setGravity(Gravity.CENTER); content.addView(prompt);
        EditText name = edit("نام کاربری", "", InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText phone = edit("شماره تماس", "", InputType.TYPE_CLASS_PHONE);
        content.addView(name); content.addView(phone);
        addButton("ورود به برنامه  ›",GOLD,() -> {
            String n=name.getText().toString().trim(), p=phone.getText().toString().trim();
            if(n.isEmpty() || p.isEmpty()) { toast("نام و شماره تماس را وارد کنید"); return; }
            userName=n; userPhone=p;
            getSharedPreferences("profile",MODE_PRIVATE).edit().putString("name",n).putString("phone",p).apply();
            hideKeyboard(); showHome();
        });
        TextView foot=text("اطلاعات شما فقط روی همین دستگاه ذخیره می‌شود",12,muted(),false); foot.setGravity(Gravity.CENTER); content.addView(foot);
    }

    private void showHome() {
        page="home"; setup("خانه");
        LinearLayout welcome=column(); welcome.setPadding(dp(16),dp(15),dp(16),dp(15));
        welcome.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{Color.rgb(14,45,72),Color.rgb(5,19,36)}));
        TextView hi=text("سلام "+userName+" عزیز 👋",22,GOLD,true); welcome.addView(hi);
        welcome.addView(text("به سرویس‌یار گلدرانی خوش آمدید",14,WHITE,false));
        TextView badge=text("GOLDARANI  •  ELEVATOR SERVICE",10,MUTED,true); badge.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL); welcome.addView(badge);
        card(content,welcome);
        TextView section=text("مدیریت خدمات شما",17,txt(),true); section.setPadding(dp(2),dp(10),dp(2),dp(5)); content.addView(section);
        LinearLayout grid1=row(); grid1.setWeightSum(2);
        gridTile(grid1,"🔧","ثبت سرویس",BLUE,()->showForm("services"));
        gridTile(grid1,"🏢","ساختمان‌ها",TEAL,()->showList("buildings"));
        content.addView(grid1);
        LinearLayout grid2=row(); grid2.setWeightSum(2);
        gridTile(grid2,"💰","طلب‌ها",ORANGE,()->showList("debts"));
        gridTile(grid2,"⚙","تنظیمات",PURPLE,()->showSettings());
        content.addView(grid2);
        TextView summary=text("خلاصه وضعیت",16,txt(),true); summary.setPadding(dp(2),dp(14),dp(2),dp(5)); content.addView(summary);
        LinearLayout stats=row();
        stat(stats,"سرویس‌ها",db.count("services"),BLUE); stat(stats,"ساختمان‌ها",db.count("buildings"),TEAL); stat(stats,"طلب‌ها",db.count("debts"),ORANGE);
        content.addView(stats);
        LinearLayout support=column(); support.addView(text("پشتیبانی اختصاصی",15,txt(),true)); support.addView(text("0912-468-2580",15,GOLD,true));
        card(content,support);
        addButton("تماس با پشتیبانی",PANEL2,()->dial("09124682580"));
        addBottomNav();
    }

    private void gridTile(LinearLayout parent,String emoji,String label,int color,Runnable click) {
        LinearLayout tile=column(); tile.setGravity(Gravity.CENTER); tile.setPadding(dp(6),dp(14),dp(6),dp(14)); tile.setBackground(shape(color,18,0));
        TextView ic=text(emoji,25,Color.WHITE,true); ic.setGravity(Gravity.CENTER); tile.addView(ic);
        TextView tx=text(label,14,Color.WHITE,true); tx.setGravity(Gravity.CENTER); tile.addView(tx);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(112),1); lp.setMargins(dp(4),dp(4),dp(4),dp(4)); parent.addView(tile,lp);
        tile.setOnClickListener(v->click.run());
    }

    private void stat(LinearLayout parent,String label,int count,int color) {
        LinearLayout box=column(); box.setGravity(Gravity.CENTER); box.setPadding(dp(4),dp(9),dp(4),dp(9)); box.setBackground(shape(panel(),14,color));
        TextView n=text(""+count,22,color,true); n.setGravity(Gravity.CENTER); box.addView(n);
        TextView l=text(label,11,txt(),false); l.setGravity(Gravity.CENTER); box.addView(l);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1); lp.setMargins(dp(3),0,dp(3),0); parent.addView(box,lp);
    }

    private void addBottomNav() {
        LinearLayout nav=row(); nav.setPadding(dp(5),dp(7),dp(5),dp(7)); nav.setBackgroundColor(dark?Color.rgb(5,17,32):Color.rgb(225,234,243));
        navItem(nav,"⌂","خانه",()->showHome()); navItem(nav,"🔧","سرویس",()->showForm("services"));
        navItem(nav,"▦","ساختمان",()->showList("buildings")); navItem(nav,"◉","طلب‌ها",()->showList("debts")); navItem(nav,"⚙","تنظیمات",()->showSettings());
        root.addView(nav);
    }
    private void navItem(LinearLayout nav,String icon,String label,Runnable r) {
        LinearLayout item=column(); item.setGravity(Gravity.CENTER);
        TextView a=text(icon,17,GOLD,true); a.setGravity(Gravity.CENTER); item.addView(a);
        TextView b=text(label,10,txt(),false); b.setGravity(Gravity.CENTER); item.addView(b);
        nav.addView(item,new LinearLayout.LayoutParams(0,-2,1)); item.setOnClickListener(v->r.run());
    }

    private void showForm(String type) {
        page=type; boolean service=type.equals("services");
        setup(service?"ثبت سرویس جدید":"ثبت اطلاعات");
        content.addView(text(service?"اطلاعات سرویس را وارد کنید":"اطلاعات ساختمان را وارد کنید",17,txt(),true));
        EditText title=edit(service?"نام ساختمان / مشتری":"نام ساختمان","",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText date=edit(service?"تاریخ سرویس (مثلاً 1405/07/01)":"آدرس ساختمان","",InputType.TYPE_CLASS_TEXT);
        EditText details=edit(service?"نوع سرویس و توضیحات":"تعداد طبقات / توضیحات","",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        content.addView(title); content.addView(date); content.addView(details);
        addButton(service?"ثبت سرویس ✓":"ثبت ساختمان ✓",BLUE,()->{
            String a=title.getText().toString().trim(), b=date.getText().toString().trim(), c=details.getText().toString().trim();
            if(a.isEmpty()) {toast("نام را وارد کنید");return;}
            db.insert(type,a,b,c);
            hideKeyboard(); toast(service?"سرویس ثبت شد":"ساختمان ثبت شد"); showList(type);
        });
        if(service) addButton("مشاهده سرویس‌های ثبت‌شده",PANEL2,()->showList("services"));
    }

    private void showList(String type) {
        page=type; String title=type.equals("services")?"سرویس‌ها":type.equals("buildings")?"ساختمان‌ها":"طلب‌ها";
        setup(title);
        LinearLayout header=row(); header.addView(text("فهرست "+title,18,txt(),true),new LinearLayout.LayoutParams(0,-2,1));
        TextView plus=text("+",26,GOLD,true); plus.setGravity(Gravity.CENTER); plus.setBackground(shape(panel(),30,GOLD)); header.addView(plus,new LinearLayout.LayoutParams(dp(44),dp(44)));
        plus.setOnClickListener(v->{if(type.equals("debts")) showDebtForm(); else showForm(type);});
        content.addView(header);
        if(type.equals("debts")) addButton("ثبت طلب جدید  +",ORANGE,()->showDebtForm());
        Cursor c=db.list(type);
        if(c.getCount()==0) {
            LinearLayout empty=column(); empty.setGravity(Gravity.CENTER); empty.setPadding(dp(15),dp(30),dp(15),dp(30));
            TextView e=text("▤",36,GOLD,true);e.setGravity(Gravity.CENTER);empty.addView(e);
            TextView msg=text("هنوز موردی ثبت نشده است",15,muted(),false);msg.setGravity(Gravity.CENTER);empty.addView(msg);card(content,empty);
        }
        while(c.moveToNext()) {
            final int id=c.getInt(0); String a=c.getString(1), b=c.getString(2), d=c.getString(3);
            LinearLayout item=column();
            TextView nm=text(a,16,txt(),true); item.addView(nm);
            if(b!=null&&!b.isEmpty()) item.addView(text(b,13,muted(),false));
            if(d!=null&&!d.isEmpty()) item.addView(text(d,13,muted(),false));
            TextView del=text("حذف مورد",12,Color.rgb(255,110,110),true); del.setGravity(Gravity.RIGHT); item.addView(del);
            del.setOnClickListener(v->confirmDelete(id,type));
            card(content,item);
        }
        c.close();
        addButton("بازگشت به خانه",PANEL2,()->showHome());
    }

    private void showDebtForm() {
        setup("ثبت طلب جدید");
        content.addView(text("اطلاعات طلب",17,txt(),true));
        EditText who=edit("نام مشتری یا ساختمان","",InputType.TYPE_CLASS_TEXT);
        EditText amount=edit("مبلغ طلب (تومان)","",InputType.TYPE_CLASS_NUMBER);
        EditText note=edit("توضیحات / تاریخ","",InputType.TYPE_CLASS_TEXT);
        content.addView(who);content.addView(amount);content.addView(note);
        addButton("ثبت طلب ✓",ORANGE,()->{
            String a=who.getText().toString().trim(), b=amount.getText().toString().trim(), c=note.getText().toString().trim();
            if(a.isEmpty()||b.isEmpty()){toast("نام و مبلغ را وارد کنید");return;}
            db.insert("debts",a,b+" تومان",c);hideKeyboard();toast("طلب ثبت شد");showList("debts");
        });
    }

    private void confirmDelete(int id,String type) {
        new AlertDialog.Builder(this)
            .setTitle(userName+" عزیز")
            .setMessage("از حذف اطلاعات مطمئن هستید؟\nاین عملیات قابل بازگشت نیست.")
            .setPositiveButton("حذف", (d,w)->{db.delete(id,type);showList(type);})
            .setNegativeButton("انصراف",null)
            .show();
    }

    private void showSettings() {
        page="settings"; setup("تنظیمات");
        LinearLayout account=column(); account.addView(text("حساب کاربری",17,txt(),true));
        account.addView(text("نام کاربر: "+userName,14,muted(),false));
        account.addView(text("شماره تماس: "+userPhone,14,muted(),false)); card(content,account);
        LinearLayout theme=column(); theme.addView(text("حالت نمایش",16,txt(),true));
        theme.addView(text(dark?"حالت فعلی: شب":"حالت فعلی: روز",13,muted(),false));
        card(content,theme);
        addButton(dark?"☀ فعال‌کردن حالت روز":"☾ فعال‌کردن حالت شب",PANEL2,()->{
            dark=!dark;getSharedPreferences("profile",MODE_PRIVATE).edit().putBoolean("dark",dark).apply();showSettings();
        });
        LinearLayout fonts=column(); fonts.addView(text("اندازه نوشته‌ها",16,txt(),true)); fonts.addView(text("فعلی: "+(fontScale<0.95?"کوچک":fontScale>1.05?"بزرگ":"متوسط"),13,muted(),false)); card(content,fonts);
        addButton("الف−  کوچک‌تر",PANEL2,()->setFont(0.9f));
        addButton("الف  اندازه متوسط",PANEL2,()->setFont(1.0f));
        addButton("الف+  بزرگ‌تر",PANEL2,()->setFont(1.15f));
        LinearLayout support=column(); support.addView(text("پشتیبانی",16,txt(),true)); support.addView(text("0912-468-2580",16,GOLD,true)); card(content,support);
        addButton("تماس با پشتیبانی",TEAL,()->dial("09124682580"));
        addButton("خروج از حساب کاربری",ORANGE,()->new AlertDialog.Builder(this).setTitle(userName+" عزیز").setMessage("از حساب کاربری خارج می‌شوید؟").setPositiveButton("خروج",(d,w)->{getSharedPreferences("profile",MODE_PRIVATE).edit().remove("name").remove("phone").apply();userName="";userPhone="";showLogin();}).setNegativeButton("انصراف",null).show());
        addButton("بازگشت به خانه",BLUE,()->showHome());
    }

    private void setFont(float f) {fontScale=f;getSharedPreferences("profile",MODE_PRIVATE).edit().putFloat("font",f).apply();showSettings();}
    private void dial(String number) {try{startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number)));}catch(Exception e){toast("امکان باز کردن شماره‌گیر وجود ندارد");}}
    private void toast(String s) {Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    private void hideKeyboard() {try{((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(root.getWindowToken(),0);}catch(Exception ignored){}}

    private class Db extends SQLiteOpenHelper {
        Db(Context c){super(c,"goldarani_local.db",null,1);}
        @Override public void onCreate(SQLiteDatabase d) {
            d.execSQL("CREATE TABLE services (_id INTEGER PRIMARY KEY AUTOINCREMENT, a TEXT, b TEXT, c TEXT)");
            d.execSQL("CREATE TABLE buildings (_id INTEGER PRIMARY KEY AUTOINCREMENT, a TEXT, b TEXT, c TEXT)");
            d.execSQL("CREATE TABLE debts (_id INTEGER PRIMARY KEY AUTOINCREMENT, a TEXT, b TEXT, c TEXT)");
        }
        @Override public void onUpgrade(SQLiteDatabase d,int oldV,int newV){}
        void insert(String table,String a,String b,String c){getWritableDatabase().execSQL("INSERT INTO "+table+"(a,b,c) VALUES(?,?,?)",new Object[]{a,b,c});}
        void delete(int id,String table){getWritableDatabase().delete(table,"_id=?",new String[]{String.valueOf(id)});}
        Cursor list(String table){return getReadableDatabase().rawQuery("SELECT _id,a,b,c FROM "+table+" ORDER BY _id DESC",null);}
        int count(String table){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM "+table,null);int n=0;if(c.moveToFirst())n=c.getInt(0);c.close();return n;}
    }
}

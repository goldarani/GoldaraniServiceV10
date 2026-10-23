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

        // Glass-style welcome panel with a premium navy/gold palette.
        LinearLayout welcome=column();
        welcome.setPadding(dp(18),dp(18),dp(18),dp(18));
        GradientDrawable heroBg=new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{Color.rgb(24,65,101),Color.rgb(10,31,54),Color.rgb(5,18,34)});
        heroBg.setCornerRadius(dp(24));
        heroBg.setStroke(dp(1),Color.rgb(71,126,171));
        welcome.setBackground(heroBg);

        LinearLayout brandRow=row();
        LinearLayout logo=column(); logo.setGravity(Gravity.CENTER);
        logo.setBackground(shape(Color.rgb(246,190,79),17,Color.rgb(255,224,151)));
        TextView logoIcon=text("⇅",27,Color.rgb(7,22,41),true); logoIcon.setGravity(Gravity.CENTER);
        logo.addView(logoIcon,new LinearLayout.LayoutParams(dp(54),dp(54)));
        brandRow.addView(logo,new LinearLayout.LayoutParams(dp(58),dp(58)));
        LinearLayout brandText=column(); brandText.setPadding(dp(12),0,0,0);
        brandText.addView(text("سرویس‌یار گلدرانی",21,GOLD,true));
        brandText.addView(text("مدیریت هوشمند سرویس آسانسور",13,WHITE,false));
        brandRow.addView(brandText,new LinearLayout.LayoutParams(0,-2,1));
        welcome.addView(brandRow);

        View gap=new View(this); welcome.addView(gap,new LinearLayout.LayoutParams(1,dp(12)));
        welcome.addView(text("سلام "+userName+" عزیز 👋",19,WHITE,true));
        welcome.addView(text("امروز هم کارهایت را مرتب و سریع مدیریت کن.",13,Color.rgb(190,211,232),false));
        TextView badge=text("GOLDARANI  •  ELEVATOR SERVICE",10,GOLD,true);
        badge.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL); badge.setPadding(0,dp(10),0,0); welcome.addView(badge);
        card(content,welcome);

        LinearLayout sectionRow=row();
        sectionRow.addView(text("دسترسی سریع",17,txt(),true),new LinearLayout.LayoutParams(0,-2,1));
        TextView sectionTag=text("پنل مدیریت",11,GOLD,true); sectionTag.setGravity(Gravity.CENTER);
        sectionTag.setPadding(dp(10),dp(5),dp(10),dp(5)); sectionTag.setBackground(shape(dark?Color.rgb(24,49,70):Color.rgb(255,247,225),20,dark?Color.rgb(91,120,146):Color.rgb(226,194,125)));
        sectionRow.addView(sectionTag); content.addView(sectionRow);

        LinearLayout grid1=row(); grid1.setWeightSum(2);
        gridTile(grid1,"▦","ثبت پرونده ساختمان",BLUE,()->showForm("services"));
        gridTile(grid1,"◷","سرویس‌های امروز",TEAL,()->showServiceList("today")); content.addView(grid1);
        LinearLayout grid2=row(); grid2.setWeightSum(2);
        gridTile(grid2,"✓","انجام‌شده‌ها",PURPLE,()->showServiceList("completed"));
        gridTile(grid2,"▤","همه پرونده‌ها",Color.rgb(40,105,151),()->showServiceList("all")); content.addView(grid2);
        LinearLayout grid3=row(); grid3.setWeightSum(2);
        gridTile(grid3,"◉","طلب‌ها",ORANGE,()->showList("debts"));
        gridTile(grid3,"⚙","تنظیمات",Color.rgb(75,91,130),()->showSettings()); content.addView(grid3);

        TextView summaryTitle=text("خلاصه وضعیت",17,txt(),true);
        summaryTitle.setPadding(dp(2),dp(14),dp(2),dp(5)); content.addView(summaryTitle);
        LinearLayout stats=row();
        stat(stats,"پرونده‌ها",db.count("services"),BLUE);
        stat(stats,"طلب‌های ثبت‌شده",db.count("debts"),ORANGE); content.addView(stats);

        LinearLayout due=column(); due.setPadding(dp(16),dp(14),dp(16),dp(14));
        GradientDrawable dueBg=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{Color.rgb(30,62,85),Color.rgb(12,33,53)});
        dueBg.setCornerRadius(dp(20)); dueBg.setStroke(dp(1),Color.rgb(192,151,67)); due.setBackground(dueBg);
        LinearLayout dueHead=row();
        TextView coin=text("◉",24,GOLD,true); coin.setGravity(Gravity.CENTER); dueHead.addView(coin,new LinearLayout.LayoutParams(dp(38),dp(38)));
        LinearLayout dueLabels=column(); dueLabels.setPadding(dp(9),0,0,0);
        dueLabels.addView(text("مجموع طلب‌های ثبت‌شده",13,WHITE,true));
        dueLabels.addView(text("فقط طلب‌هایی که جداگانه ثبت شده‌اند",11,Color.rgb(185,205,224),false));
        dueHead.addView(dueLabels,new LinearLayout.LayoutParams(0,-2,1)); due.addView(dueHead);
        TextView total=text(money(db.totalDebts())+" تومان",25,GOLD,true); total.setPadding(0,dp(8),0,0); due.addView(total);
        LinearLayout.LayoutParams dueLp=new LinearLayout.LayoutParams(-1,-2); dueLp.setMargins(0,dp(7),0,dp(7)); content.addView(due,dueLp);

        LinearLayout support=column(); support.setPadding(dp(14),dp(12),dp(14),dp(12));
        support.addView(text("نیاز به راهنمایی داری؟",14,txt(),true));
        support.addView(text("پشتیبانی سرویس‌یار گلدرانی  •  0912-468-2580",12,muted(),false));
        card(content,support);
        addButton("تماس با پشتیبانی  ↗",PANEL2,()->dial("09124682580"));
        addBottomNav();
    }

    private void gridTile(LinearLayout parent,String emoji,String label,int color,Runnable click) {
        LinearLayout tile=column(); tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(8),dp(13),dp(8),dp(13));
        GradientDrawable glass=new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            new int[]{dark?Color.rgb(27,58,83):Color.rgb(255,255,255),dark?Color.rgb(12,34,55):Color.rgb(232,241,249)});
        glass.setCornerRadius(dp(20));
        glass.setStroke(dp(1),dark?Color.rgb(55,94,125):Color.rgb(203,219,233));
        tile.setBackground(glass);
        TextView ic=text(emoji,24,color,true); ic.setGravity(Gravity.CENTER);
        ic.setBackground(shape(dark?Color.rgb(25,55,78):Color.rgb(235,243,250),16,color));
        LinearLayout.LayoutParams iconLp=new LinearLayout.LayoutParams(dp(48),dp(48)); iconLp.bottomMargin=dp(8); tile.addView(ic,iconLp);
        TextView tx=text(label,13,txt(),true); tx.setGravity(Gravity.CENTER); tile.addView(tx);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(108),1); lp.setMargins(dp(4),dp(4),dp(4),dp(4)); parent.addView(tile,lp);
        tile.setOnClickListener(v->click.run());
    }

    private void stat(LinearLayout parent,String label,int count,int color) {
        LinearLayout box=column(); box.setGravity(Gravity.CENTER); box.setPadding(dp(8),dp(12),dp(8),dp(12));
        GradientDrawable glass=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{dark?Color.rgb(24,52,76):Color.WHITE,dark?Color.rgb(11,31,51):Color.rgb(231,240,248)});
        glass.setCornerRadius(dp(17)); glass.setStroke(dp(1),dark?Color.rgb(50,83,111):Color.rgb(207,220,232)); box.setBackground(glass);
        TextView n=text(""+count,23,color,true); n.setGravity(Gravity.CENTER); box.addView(n);
        TextView l=text(label,11,txt(),false); l.setGravity(Gravity.CENTER); box.addView(l);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1); lp.setMargins(dp(3),0,dp(3),0); parent.addView(box,lp);
    }

    private void addBottomNav() {
        LinearLayout nav=row(); nav.setPadding(dp(5),dp(7),dp(5),dp(7));
        GradientDrawable navBg=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{dark?Color.rgb(13,34,54):Color.rgb(250,252,255),dark?Color.rgb(5,17,32):Color.rgb(225,234,243)});
        navBg.setStroke(dp(1),dark?Color.rgb(44,76,103):Color.rgb(208,221,234));
        navBg.setCornerRadius(dp(20)); nav.setBackground(navBg);
        navItem(nav,"⌂","خانه",()->showHome()); navItem(nav,"▦","پرونده‌ها",()->showServiceList("all"));
        navItem(nav,"＋","ثبت",()->showForm("services")); navItem(nav,"◉","طلب‌ها",()->showList("debts")); navItem(nav,"⚙","تنظیمات",()->showSettings());
        root.addView(nav);
    }
    private void navItem(LinearLayout nav,String icon,String label,Runnable r) {
        LinearLayout item=column(); item.setGravity(Gravity.CENTER); item.setPadding(0,dp(2),0,dp(2));
        TextView a=text(icon,18,GOLD,true); a.setGravity(Gravity.CENTER); item.addView(a);
        TextView b=text(label,10,txt(),false); b.setGravity(Gravity.CENTER); item.addView(b);
        nav.addView(item,new LinearLayout.LayoutParams(0,-2,1)); item.setOnClickListener(v->r.run());
    }

    private void showForm(String type) {
        page="services"; setup("ثبت ساختمان و سرویس");
        content.addView(text("اطلاعات ساختمان و برنامه سرویس را یک‌جا ثبت کنید",17,txt(),true));
        EditText title=edit("نام ساختمان","",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText address=edit("آدرس کامل ساختمان","",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        EditText manager=edit("نام مدیر ساختمان","",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText phone=edit("شماره تماس مدیر ساختمان","",InputType.TYPE_CLASS_PHONE);
        EditText date=edit("تاریخ سرویس (شمسی: 1405/07/01)",todayJalali(),InputType.TYPE_CLASS_TEXT);
        EditText serviceAmount=edit("مبلغ سرویس دوره‌ای (تومان)","",InputType.TYPE_CLASS_NUMBER);
        EditText repairDetails=edit("شرح خرابی / تعمیرات (در صورت وجود)","",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        EditText repairAmount=edit("مبلغ تعمیرات (تومان)","",InputType.TYPE_CLASS_NUMBER);
        EditText notes=edit("توضیحات تکمیلی (اختیاری)","",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        content.addView(title); content.addView(address); content.addView(manager); content.addView(phone);
        content.addView(date); content.addView(serviceAmount); content.addView(repairDetails); content.addView(repairAmount); content.addView(notes);
        addButton("ثبت اطلاعات ساختمان و سرویس ✓",BLUE,()->{
            String a=title.getText().toString().trim(), addr=address.getText().toString().trim(), mgr=manager.getText().toString().trim(), ph=phone.getText().toString().trim(), dt=date.getText().toString().trim();
            if(a.isEmpty()) {toast("نام ساختمان را وارد کنید");return;}
            if(addr.isEmpty()) {toast("آدرس ساختمان را وارد کنید");return;}
            if(mgr.isEmpty()) {toast("نام مدیر ساختمان را وارد کنید");return;}
            if(ph.isEmpty()) {toast("شماره مدیر ساختمان را وارد کنید");return;}
            if(!validDate(dt)) {toast("تاریخ را به شکل 1405/07/01 وارد کنید");return;}
            long serviceCost=parseAmount(serviceAmount.getText().toString().trim());
            long repairCost=parseAmount(repairAmount.getText().toString().trim());
            String extra=notes.getText().toString().trim();
            String desc=extra;
            db.insertService(a,normalizeJalaliDate(dt),desc,serviceCost,repairDetails.getText().toString().trim(),repairCost,addr,mgr,ph);
            hideKeyboard(); toast("اطلاعات ساختمان و سرویس ثبت شد"); showServiceList("pending");
        });
        addButton("مشاهده فهرست ساختمان‌ها و سرویس‌ها",PANEL2,()->showServiceList("all"));
    }

    private void showList(String type) {
        if(type.equals("services")) { showServiceList("all"); return; }
        page=type; String title=type.equals("services")?"سرویس‌ها":type.equals("buildings")?"ساختمان‌ها":"طلب‌ها";
        setup(title);
        LinearLayout header=row(); header.addView(text("فهرست "+title,18,txt(),true),new LinearLayout.LayoutParams(0,-2,1));
        TextView plus=text("+",26,GOLD,true); plus.setGravity(Gravity.CENTER); plus.setBackground(shape(panel(),30,GOLD)); header.addView(plus,new LinearLayout.LayoutParams(dp(44),dp(44)));
        plus.setOnClickListener(v->{if(type.equals("debts")) showDebtForm(); else showForm(type);});
        content.addView(header);
        if(type.equals("debts")) {
            LinearLayout total=column(); total.addView(text("مجموع طلب‌ها",14,muted(),true)); total.addView(text(money(db.totalDebts())+" تومان",24,GOLD,true)); card(content,total);
            addButton("ثبت طلب جدید  +",ORANGE,()->showDebtForm());
        }
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
            if(b!=null&&!b.isEmpty()) item.addView(text(type.equals("debts")?"مبلغ: "+money(parseAmount(b))+" تومان":b,13,type.equals("debts")?GOLD:muted(),type.equals("debts")));
            if(d!=null&&!d.isEmpty()) item.addView(text(d,13,muted(),false));
            TextView del=text("حذف مورد",12,Color.rgb(255,110,110),true); del.setGravity(Gravity.RIGHT); item.addView(del);
            del.setOnClickListener(v->confirmDelete(id,type));
            card(content,item);
        }
        c.close();
        addButton("بازگشت به خانه",PANEL2,()->showHome());
    }

    private void showServiceList(String filter) {
        page="services"; setup(filter.equals("completed")?"سرویس‌های انجام‌شده":filter.equals("today")?"سرویس‌های امروز":filter.equals("pending")?"سرویس‌های انجام‌نشده":"پرونده‌های ساختمان و سرویس");
        LinearLayout tabs=row();
        tab(tabs,"امروز",filter.equals("today"),()->showServiceList("today"));
        tab(tabs,"انجام‌نشده",filter.equals("pending"),()->showServiceList("pending"));
        tab(tabs,"انجام‌شده",filter.equals("completed"),()->showServiceList("completed"));
        tab(tabs,"همه",filter.equals("all"),()->showServiceList("all"));
        content.addView(tabs);
        LinearLayout summary=column();
        summary.addView(text("امروز: "+todayJalali(),14,GOLD,true));
        summary.addView(text("سرویس‌های باز امروز: "+db.countTodayServices(todayJalali()),13,muted(),false));
        card(content,summary);
        Cursor c=db.serviceList(filter,todayJalali());
        if(c.getCount()==0) {
            LinearLayout empty=column(); empty.setGravity(Gravity.CENTER); empty.setPadding(dp(15),dp(28),dp(15),dp(28));
            TextView e=text(filter.equals("completed")?"✓":"📅",34,GOLD,true);e.setGravity(Gravity.CENTER);empty.addView(e);
            TextView msg=text(filter.equals("today")?"برای امروز سرویسی ثبت نشده است":filter.equals("completed")?"هنوز سرویسی انجام‌شده ثبت نشده است":filter.equals("pending")?"سرویس انجام‌نشده‌ای وجود ندارد":"هنوز سرویسی ثبت نشده است",14,muted(),false);msg.setGravity(Gravity.CENTER);empty.addView(msg);card(content,empty);
        }
        while(c.moveToNext()) {
            final int id=c.getInt(0); String a=c.getString(1), date=c.getString(2), details=c.getString(3); boolean done=c.getInt(4)==1;
            long serviceCost=c.getLong(5), repairCost=c.getLong(7); String repairDetails=c.getString(6); String address=c.getString(8), manager=c.getString(9), phone=c.getString(10);
            LinearLayout item=column();
            LinearLayout titleRow=row();
            TextView nm=text(a,16,txt(),true); titleRow.addView(nm,new LinearLayout.LayoutParams(0,-2,1));
            TextView badge=text(done?"انجام شد":"در انتظار",11,done?TEAL:ORANGE,true); badge.setGravity(Gravity.CENTER); badge.setPadding(dp(9),dp(5),dp(9),dp(5)); badge.setBackground(shape(done?Color.rgb(16,91,86):Color.rgb(105,61,25),12,0)); titleRow.addView(badge);
            item.addView(titleRow);
            item.addView(text("👤 مدیر ساختمان: "+(manager==null?"—":manager),13,txt(),true));
            item.addView(text("📞 شماره مدیر: "+(phone==null?"—":phone),13,muted(),false));
            item.addView(text("📍 آدرس: "+(address==null?"—":address),13,muted(),false));
            item.addView(text("📅 تاریخ سرویس: "+(date==null?"—":date),13,GOLD,true));
            if(details!=null&&!details.isEmpty()) item.addView(text("توضیحات: "+details,13,muted(),false));
            item.addView(text("مبلغ سرویس: "+money(serviceCost)+" تومان",13,GOLD,true));
            if(repairDetails!=null&&!repairDetails.isEmpty()) item.addView(text("شرح خرابی: "+repairDetails,13,muted(),false));
            if(repairCost>0) item.addView(text("مبلغ تعمیرات: "+money(repairCost)+" تومان",13,ORANGE,true));
            item.addView(text("جمع این سرویس: "+money(serviceCost+repairCost)+" تومان",14,GOLD,true));
            LinearLayout actions=row();
            if(!done) smallAction(actions,"✓ انجام شد",TEAL,()->new AlertDialog.Builder(this).setTitle("تأیید انجام سرویس").setMessage("آیا سرویس «"+a+"» انجام شده است؟").setPositiveButton("بله، انجام شد",(d,w)->{db.markServiceDone(id);showServiceList(filter);}).setNegativeButton("انصراف",null).show());
            smallAction(actions,"حذف",Color.rgb(165,54,64),()->confirmDelete(id,"services"));
            item.addView(actions); card(content,item);
        }
        c.close();
        addButton("+ ثبت پرونده ساختمان و سرویس",BLUE,()->showForm("services"));
        addButton("بازگشت به خانه",PANEL2,()->showHome());
    }

    private void tab(LinearLayout parent,String label,boolean selected,Runnable action) {
        TextView t=text(label,12,selected?Color.rgb(8,24,42):txt(),true); t.setGravity(Gravity.CENTER); t.setPadding(dp(5),dp(11),dp(5),dp(11));
        t.setBackground(shape(selected?GOLD:panel(),13,selected?GOLD:Color.rgb(50,76,99)));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1); lp.setMargins(dp(2),dp(5),dp(2),dp(8)); parent.addView(t,lp); t.setOnClickListener(v->action.run());
    }

    private void smallAction(LinearLayout parent,String label,int color,Runnable action) {
        TextView t=text(label,12,Color.WHITE,true); t.setGravity(Gravity.CENTER); t.setPadding(dp(12),dp(8),dp(12),dp(8)); t.setBackground(shape(color,12,0));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1); lp.setMargins(dp(3),dp(8),dp(3),0); parent.addView(t,lp); t.setOnClickListener(v->action.run());
    }

    private String money(long amount) {
        java.text.NumberFormat f=java.text.NumberFormat.getInstance(new java.util.Locale("fa","IR"));
        return f.format(amount);
    }

    private String normalizeJalaliDate(String value) {
        String d=value.trim().replace('-', '/'); StringBuilder ascii=new StringBuilder();
        for(int i=0;i<d.length();i++){char ch=d.charAt(i); if(ch>='۰'&&ch<='۹') ascii.append((char)(ch-'۰'+'0')); else if(ch>='٠'&&ch<='٩') ascii.append((char)(ch-'٠'+'0')); else ascii.append(ch);}
        String[] parts=ascii.toString().split("/");
        if(parts.length!=3)return ascii.toString();
        try{return String.format(java.util.Locale.US,"%04d/%02d/%02d",Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),Integer.parseInt(parts[2]));}catch(Exception e){return ascii.toString();}
    }

    private boolean validDate(String value) {
        if(value==null) return false;
        String d=value.trim().replace('-', '/').replace('٫','/');
        return d.matches("\\d{4}/\\d{1,2}/\\d{1,2}") || d.matches("[۰-۹]{4}/[۰-۹]{1,2}/[۰-۹]{1,2}");
    }

    private String todayJalali() {
        java.util.Calendar cal=java.util.Calendar.getInstance();
        int gy=cal.get(java.util.Calendar.YEAR), gm=cal.get(java.util.Calendar.MONTH)+1, gd=cal.get(java.util.Calendar.DAY_OF_MONTH);
        int[] gdm={0,31,28,31,30,31,30,31,31,30,31,30,31};
        boolean leap=(gy%4==0 && gy%100!=0)||gy%400==0; if(leap) gdm[2]=29;
        int gy2=gy-1600, gm2=gm-1, gd2=gd-1;
        int g_day_no=365*gy2+(gy2+3)/4-(gy2+99)/100+(gy2+399)/400;
        for(int i=0;i<gm2;i++) g_day_no+=gdm[i+1]; g_day_no+=gd2;
        int j_day_no=g_day_no-79; int j_np=j_day_no/12053; j_day_no%=12053;
        int jy=979+33*j_np+4*(j_day_no/1461); j_day_no%=1461;
        if(j_day_no>=366){jy+=(j_day_no-1)/365;j_day_no=(j_day_no-1)%365;}
        int[] jdm={31,31,31,31,31,31,30,30,30,30,30,29}; int jm=0;
        while(jm<11 && j_day_no>=jdm[jm]) {j_day_no-=jdm[jm];jm++;}
        int jd=j_day_no+1;
        return String.format(java.util.Locale.US,"%04d/%02d/%02d",jy,jm+1,jd);
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
            db.insert("debts",a,b,c);hideKeyboard();toast("طلب ثبت شد");showList("debts");
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

    private long parseAmount(String raw) { if(raw==null)return 0; StringBuilder digits=new StringBuilder(); for(int i=0;i<raw.length();i++){char ch=raw.charAt(i); if(ch>='۰'&&ch<='۹') digits.append((char)(ch-'۰'+'0')); else if(ch>='٠'&&ch<='٩') digits.append((char)(ch-'٠'+'0')); else if(ch>='0'&&ch<='9') digits.append(ch); } try{return Long.parseLong(digits.toString());}catch(Exception e){return 0;} }

    private class Db extends SQLiteOpenHelper {
        Db(Context c){super(c,"goldarani_local.db",null,4);}
        @Override public void onCreate(SQLiteDatabase d) {
            d.execSQL("CREATE TABLE services (_id INTEGER PRIMARY KEY AUTOINCREMENT, a TEXT, b TEXT, c TEXT, completed INTEGER NOT NULL DEFAULT 0, service_amount INTEGER NOT NULL DEFAULT 0, repair_details TEXT, repair_amount INTEGER NOT NULL DEFAULT 0, address TEXT, manager TEXT, manager_phone TEXT)");
            d.execSQL("CREATE TABLE buildings (_id INTEGER PRIMARY KEY AUTOINCREMENT, a TEXT, b TEXT, c TEXT)");
            d.execSQL("CREATE TABLE debts (_id INTEGER PRIMARY KEY AUTOINCREMENT, a TEXT, b TEXT, c TEXT)");
        }
        @Override public void onUpgrade(SQLiteDatabase d,int oldV,int newV){
            if(oldV<2) d.execSQL("ALTER TABLE services ADD COLUMN completed INTEGER NOT NULL DEFAULT 0");
            if(oldV<3) {
                d.execSQL("ALTER TABLE services ADD COLUMN service_amount INTEGER NOT NULL DEFAULT 0");
                d.execSQL("ALTER TABLE services ADD COLUMN repair_details TEXT");
                d.execSQL("ALTER TABLE services ADD COLUMN repair_amount INTEGER NOT NULL DEFAULT 0");
            }
            if(oldV<4) {
                d.execSQL("ALTER TABLE services ADD COLUMN address TEXT");
                d.execSQL("ALTER TABLE services ADD COLUMN manager TEXT");
                d.execSQL("ALTER TABLE services ADD COLUMN manager_phone TEXT");
            }
        }
        void insert(String table,String a,String b,String c){getWritableDatabase().execSQL("INSERT INTO "+table+"(a,b,c) VALUES(?,?,?)",new Object[]{a,b,c});}
        void insertService(String a,String date,String details,long serviceAmount,String repairDetails,long repairAmount,String address,String manager,String phone){
            getWritableDatabase().execSQL("INSERT INTO services(a,b,c,completed,service_amount,repair_details,repair_amount,address,manager,manager_phone) VALUES(?,?,?,0,?,?,?,?,?,?)",new Object[]{a,date,details,serviceAmount,repairDetails,repairAmount,address,manager,phone});
        }
        void delete(int id,String table){getWritableDatabase().delete(table,"_id=?",new String[]{String.valueOf(id)});}
        Cursor list(String table){return getReadableDatabase().rawQuery("SELECT _id,a,b,c FROM "+table+" ORDER BY _id DESC",null);}
        Cursor serviceList(String filter,String today){
            String where="";
            if(filter.equals("today")) where=" WHERE completed=0 AND b=?";
            else if(filter.equals("pending")) where=" WHERE completed=0";
            else if(filter.equals("completed")) where=" WHERE completed=1";
            String sql="SELECT _id,a,b,c,completed,service_amount,repair_details,repair_amount,address,manager,manager_phone FROM services"+where+" ORDER BY b ASC, _id DESC";
            return filter.equals("today") ? getReadableDatabase().rawQuery(sql,new String[]{today}) : getReadableDatabase().rawQuery(sql,null);
        }
        void markServiceDone(int id){getWritableDatabase().execSQL("UPDATE services SET completed=1 WHERE _id=?",new Object[]{id});}
        int countTodayServices(String today){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM services WHERE completed=0 AND b=?",new String[]{today});int n=0;if(c.moveToFirst())n=c.getInt(0);c.close();return n;}
        long totalDebts(){
            long total=0;
            Cursor c=getReadableDatabase().rawQuery("SELECT b FROM debts",null);
            while(c.moveToNext()) total+=parseAmount(c.getString(0));
            c.close();
            return total;
        }
        int count(String table){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM "+table,null);int n=0;if(c.moveToFirst())n=c.getInt(0);c.close();return n;}
    }
}

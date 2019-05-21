package com.mdd.ecglove;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TermsFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.main_page_terms_fragment, container, false);
        TextView tvContent = rootView.findViewById(R.id.tv_content);
        String content = ("1. TERMS OF USE<br><br>" +
                "By downloading, browsing, accessing, or using this ECGlove mobile application, you agree to be bound by these Terms and Conditions of Use. We reserve the right to amend these terms and conditions at any time. If you disagree with any of these Terms and Conditions of Use, you must immediately discontinue your access to the ECGlove and your use of the services offered. Continued use of the mobile application will constitute acceptance of these Terms and Conditions of Use, as may be amended from time to time." +
                "<br><br>2.\tDEFINITIONS<br>" +
                "<br>In these Terms and Conditions of Use, the following capitalised terms shall have the following meanings, except where the context otherwise requires:<br><br>" +
                "\"<b>Account</b>\" means an account created by a User on the mobile application as part of Registration.<br><br>" +
                "\"<b>Privacy Policy</b>\" means the privacy policy set out in Clause 14 of these Terms and Conditions of Use.<br><br>" +
                "\"<b>Register</b>\" means to create an Account on the mobile application and \"<b>Registration</b>\" means the act of creating such an Account.<br><br>" +
                "\"<b>Services</b>\" means all the services provided by ECGlove to Users, and \"<b>Service</b>\" means any one of them,<br><br>" +
                "\"<b>Users</b>\" means users of the mobile application, including you and \"<b>User</b>\" means any one of them.<br>" +
                "<br>3. PRIVACY POLICY<br><br>" +
                "Access to the ECGlove mobile application and use of the Services offered on the mobile application is subject to this Privacy Policy. By accessing the mobile application and by continuing to use the Services offered, you are deemed to have accepted this Privacy Policy, and, you are deemed to have consented to our use and disclosure of your personal information in the manner prescribed in this Privacy Policy.<br>" +
                "License to Use Material: By submitting any text or images via the mobile application, you represent that you are the owner of the material or have proper authorization from the owner of the material to use, reproduce and distribute it. You hereby grant us a worldwide, royalty-free, non-exclusive license to use the material to promote any products or services.<br>" +
                "<br>4. DISCLAIMER<br><br>" +
                "The mobile application, the Services, the information on the mobile application and use of all related facilities are provided on an \"as is, as available\" basis without any warranties whether express or implied.<br>" +
                "We do not warrant that the mobile application will always be accessible, uninterrupted, timely, secure, error free or free from computer virus or other invasive or damaging code.<br>");
        tvContent.setText(Html.fromHtml(content));
        return rootView;
    }
}

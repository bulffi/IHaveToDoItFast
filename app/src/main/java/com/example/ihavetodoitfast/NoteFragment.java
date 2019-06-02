package com.example.ihavetodoitfast;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.OnCheckedChangeListener;
import static android.widget.CompoundButton.OnClickListener;


public class NoteFragment extends Fragment {

    private static final String ARG_Note_ID = "note_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_DETAIL = 3;
    private Button mReportButton;
    private Button mRelatedPersonButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    private Note mNote;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private EditText mDetailField;
    private Button mVocalButton;

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_crime:
                NoteBook.get(getActivity()).deleteNote(mNote);
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getNoteReport(){
        String solvedString = null;
        if(mNote.isSolved()){
            solvedString = getString(R.string.note_report_solved);
        }else {
            solvedString = getString(R.string.note_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mNote.getDate()).toString();

        String related_person = mNote.getRelatedPerson();
        if(related_person==null){
            related_person = getString(R.string.note_no_related_person);
        }else {
            related_person = getString(R.string.note_related_person,related_person);
        }
        String report = getString(R.string.note_report,mNote.getTitle(),dateString,solvedString,related_person);

        return report;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK){
            return;
        }
        if(requestCode==REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mNote.setDate(date);
            updateDate();
        }else if(requestCode==REQUEST_CONTACT && data!=null){
            Uri contactUri = data.getData();
            String[] queryField = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor cursor = getActivity().getContentResolver()
                    .query(contactUri,queryField,null,null,null);
            try{
                if(cursor.getCount()==0){
                    return;
                }
                cursor.moveToFirst();
                String relatedPerson = cursor.getString(0);
                mNote.setRelatedPerson(relatedPerson);
                mRelatedPersonButton.setText(relatedPerson);
            }finally {
                cursor.close();
            }
        }else if(requestCode==REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.ihavetodoitfast.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private void updateDate() {
        SimpleDateFormat dateFormat =   new SimpleDateFormat( " yyyy - MM - dd " );
        mDateButton.setText(dateFormat.format(mNote.getDate()));
    }

    @Override
    public void onPause() {
        super.onPause();
        NoteBook.get(getActivity()).updateNote(mNote);
    }

    public static NoteFragment newInstance(UUID noteID){
        Bundle args = new Bundle();
        args.putSerializable(ARG_Note_ID,noteID);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID noteID = (UUID)getArguments().getSerializable(ARG_Note_ID);
        mNote = NoteBook.get(getActivity()).getNote(noteID);
        mPhotoFile = NoteBook.get(getActivity()).getPhotoFile(mNote);

        // initiate
        SpeechUtility.createUtility(this.getActivity(), SpeechConstant.APPID + "=5cf2b0f1");
    }
    private void updatePhotoView(){
        if(mPhotoFile==null||!mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note,container,false);
        mTitleField = (EditText)v.findViewById(R.id.note_title);
        mTitleField.setText(mNote.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDetailField = (EditText)v.findViewById(R.id.note_detail);
        mDetailField.setText(mNote.getDetail());
        mDetailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setDetail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mVocalButton = (Button)v.findViewById(R.id.vocal_input);
        mVocalButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RecognizerDialog dialog = new RecognizerDialog(v.getContext(),null);
                // set language
                dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                dialog.setParameter(SpeechConstant.ACCENT, "mandarin");
                // receive pure text
                dialog.setParameter(SpeechConstant.RESULT_TYPE, "plain");
                // filter out punctuation
                dialog.setParameter(SpeechConstant.ASR_PTT,"0");
                // set interval
                dialog.setParameter(SpeechConstant.VAD_BOS, "4000");
                dialog.setParameter(SpeechConstant.VAD_EOS, "1000");

                dialog.setListener(new RecognizerDialogListener() {
                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        String text=recognizerResult.getResultString();

                        mNote.setDetail(text);
                        mDetailField.setText(mDetailField.getText()+text);
                    }
                    @Override
                    public void onError(SpeechError speechError) {
                    }
                });
                dialog.show();
                // remove the fucking stupid link
                TextView txt = (TextView)dialog.getWindow().getDecorView().findViewWithTag("textlink");
                txt.setText("");
                // prompt
                Toast.makeText(v.getContext(), "请开始说话", Toast.LENGTH_SHORT).show();
            }
        });

        mDateButton = (Button)v.findViewById(R.id.note_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mNote.getDate());
                dialog.setTargetFragment(NoteFragment.this,REQUEST_DATE);
                dialog.show(fragmentManager,DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.note_solved);
        mSolvedCheckBox.setChecked(mNote.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mNote.setSolved(isChecked);
            }
        });
        mReportButton = (Button)v.findViewById(R.id.note_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,getNoteReport());
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.note_subject));
                intent = Intent.createChooser(intent,getString(R.string.send_note));
                startActivity(intent);
            }
        });
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mRelatedPersonButton = (Button)v.findViewById(R.id.note_related_person);
        mRelatedPersonButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if(mNote.getRelatedPerson()!=null){
            mRelatedPersonButton.setText(mNote.getRelatedPerson());
        }

        mPhotoButton = (ImageButton)v.findViewById(R.id.note_camera);
        mPhotoView = (ImageView)v.findViewById(R.id.note_photo);

        final Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mPhotoButton.setEnabled(true);
        mPhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.ihavetodoitfast.fileprovider",
                        mPhotoFile);
                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(takePhoto, PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity:cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(takePhoto,REQUEST_PHOTO);
            }
        });
        updatePhotoView();
        return v;
    }
}

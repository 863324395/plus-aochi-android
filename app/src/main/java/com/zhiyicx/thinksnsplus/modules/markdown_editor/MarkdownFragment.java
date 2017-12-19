package com.zhiyicx.thinksnsplus.modules.markdown_editor;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiyi.richtexteditorlib.SimpleRichEditor;
import com.zhiyi.richtexteditorlib.base.RichEditor;
import com.zhiyi.richtexteditorlib.view.BottomMenu;
import com.zhiyi.richtexteditorlib.view.dialogs.LinkDialog;
import com.zhiyi.richtexteditorlib.view.dialogs.PictureHandleDialog;
import com.zhiyi.richtexteditorlib.view.logiclist.MenuItem;
import com.zhiyi.richtexteditorlib.view.menuitem.AbstractBottomMenuItem;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.DaggerPhotoSelectorImplComponent;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSeletorImplModule;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.AndroidBug5497Workaround;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.data.beans.PostPublishBean;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.post.CirclePostDetailActivity;
import com.zhiyicx.thinksnsplus.modules.markdown_editor.types.ChooseCircleActivity;
import com.zhiyicx.thinksnsplus.modules.markdown_editor.types.ChooseCircleFragment;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * @author Jliuer
 * @Date 2017/11/17/13:21
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MarkdownFragment extends TSFragment<MarkdownContract.Presenter> implements
        SimpleRichEditor.OnEditorClickListener, View.OnClickListener, PhotoSelectorImpl.IPhotoBackListener,
        MarkdownContract.View, RichEditor.OnMarkdownWordResultListener {

    public static final String BUNDLE_SOURCE_DATA = "sourceId";

    @BindView(R.id.lu_bottom_menu)
    BottomMenu mBottomMenu;
    @BindView(R.id.rich_text_view)
    SimpleRichEditor mRichTextView;
    @BindView(R.id.ll_circle_container)
    LinearLayout mLlCircleContainer;
    @BindView(R.id.line)
    View mLine;
    @BindView(R.id.cb_syn_to_dynamic)
    CheckBox mCbSynToDynamic;
    @BindView(R.id.tv_name)
    TextView mCircleName;

    protected HashMap<Long, String> mInsertedImages;
    protected HashMap<Long, String> mFailedImages;

    protected PhotoSelectorImpl mPhotoSelector;
    protected ActionPopupWindow mPhotoPopupWindow;// 图片选择弹框
    protected ActionPopupWindow mCanclePopupWindow;// 取消提示选择弹框

    protected PostPublishBean mPostPublishBean;
    protected List<Integer> mImages = new ArrayList();

    protected CircleInfo mCircleInfo;

    public static MarkdownFragment newInstance(Bundle bundle) {
        MarkdownFragment markdownFragment = new MarkdownFragment();
        markdownFragment.setArguments(bundle);
        return markdownFragment;
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.publish);
    }

    @Override
    protected void setRightClick() {
        super.setRightClick();
        mPostPublishBean = new PostPublishBean();
        mRichTextView.getResultWords();
    }

    @Override
    public void onMarkdownWordResult(String title, String markdwon, String noMarkdown) {

        mPostPublishBean.setTitle(title);
        mPostPublishBean.setBody(markdwon);
        mPostPublishBean.setSummary(noMarkdown);
        mPostPublishBean.setCircle_id(mCircleInfo.getId());
        mPostPublishBean.setSync_feed(mCbSynToDynamic.isChecked() ? 1 : 0);
        mPostPublishBean.setImages(mImages.toArray(new Integer[mImages.size()]));
        mPresenter.publishPost(mPostPublishBean);
    }

    @Override
    protected boolean usePermisson() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCircleInfo = (CircleInfo) getArguments().getSerializable(BUNDLE_SOURCE_DATA);
        }
    }

    @Override
    protected void initView(View rootView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            AndroidBug5497Workaround.assistActivity(getActivity());
        }
        mInsertedImages = new HashMap<>();
        mFailedImages = new HashMap<>();
        initListener();
        mRichTextView.load();
        setSynToDynamicCbVisiable(mCircleInfo != null);
    }

    @Override
    protected void initData() {
        mPhotoSelector = DaggerPhotoSelectorImplComponent
                .builder()
                .photoSeletorImplModule(new PhotoSeletorImplModule(this, this, PhotoSelectorImpl
                        .NO_CRAFT))
                .build().photoSelectorImpl();
        if (getArguments() == null) {
            mLlCircleContainer.setVisibility(View.VISIBLE);
            mLine.setVisibility(View.VISIBLE);
            return;
        }
    }

    protected void initListener() {
        mRichTextView.setOnEditorClickListener(this);
        mRichTextView.setOnTextLengthChangeListener(length -> {

        });
        mRichTextView.setOnMarkdownWordResultListener(this);
        mRichTextView.setBottomMenu(mBottomMenu);

        mLlCircleContainer.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, ChooseCircleActivity.class);
            mActivity.startActivityForResult(intent, ChooseCircleFragment.CHOOSE_CIRCLE);
        });
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.publish_post);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLinkButtonClick() {
        showLinkDialog(LinkDialog.createLinkDialog(), false);
    }

    @Override
    public void onInsertImageButtonClick() {
        DeviceUtils.hideSoftKeyboard(mActivity.getApplication(), mRichTextView);
        initPhotoPopupWindow();
    }

    @Override
    public void onLinkClick(String name, String url) {
        showLinkDialog(LinkDialog.createLinkDialog(name, url), true);
    }

    @Override
    public void onImageClick(Long id) {
//        if (mInsertedImages.containsKey(id)) {
//            showPictureClickDialog(PictureHandleDialog.createDeleteDialog(id), new
//                    CharSequence[]{getString(R.string.delete)});
//        } else if (mFailedImages.containsKey(id)) {
//            showPictureClickDialog(PictureHandleDialog.createDeleteDialog(id),
//                    new CharSequence[]{getString(R.string.delete), getString(R.string.retry)});
//        }
    }

    @Override
    public void onTextStypeClick(boolean isSelect) {
        setSynToDynamicCbVisiable(!isSelect);
    }

    @Override
    public void getPhotoFailure(String errorMsg) {

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_markd_down;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooseCircleFragment.CHOOSE_CIRCLE) {
            if (data != null && data.getExtras().getParcelable(ChooseCircleFragment.BUNDLE_CIRCLE) != null) {
                mCircleInfo = data.getExtras().getParcelable(ChooseCircleFragment.BUNDLE_CIRCLE);
                mCircleName.setText(mCircleInfo.getName());
            }
        } else {
            mPhotoSelector.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void getPhotoSuccess(List<ImageBean> photoList) {
        if (photoList.isEmpty()) {
            return;
        }
        String path = photoList.get(0).getImgUrl();
        long id = SystemClock.currentThreadTimeMillis();
        long size[] = ImageUtils.getBitmapSize(path);
        mRichTextView.insertImage(path, id, size[0], size[1]);
        mInsertedImages.put(id, path);
        mPresenter.uploadPic(path, id);
    }

    @Override
    public void onUploading(long id, String filePath, int progress, int imgeId) {
        getActivity().runOnUiThread(() -> mRichTextView.setImageUploadProcess(id, progress, imgeId));
        if (progress == 100) {
            mImages.add(imgeId);
        }
    }

    @Override
    public void sendPostSuccess(CirclePostListBean data) {
        CirclePostDetailActivity.startActivity(getActivity(), data.getGroup_id(), data.getId(),
                false);
        getActivity().finish();
    }

    @Override
    public void onFailed(String filePath, long id) {

        getActivity().runOnUiThread(() -> {
            mRichTextView.setImageFailed(id);
            mInsertedImages.remove(id);
            mFailedImages.put(id, filePath);
        });


    }

    /**
     * @param isVisiable true  显示
     */
    protected void setSynToDynamicCbVisiable(boolean isVisiable) {
        if (mCircleInfo==null){
            return;
        }
        mCbSynToDynamic.setVisibility(isVisiable && mCircleInfo.getAllow_feed() == 1 ? View.VISIBLE : View.GONE);
    }

    /**
     * 初始化图片选择弹框
     */
    private void initPhotoPopupWindow() {

        if (mPhotoPopupWindow != null) {
            mPhotoPopupWindow.show();
            return;
        }
        mPhotoPopupWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.choose_from_photo))
                .item2Str(getString(R.string.choose_from_camera))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(() -> {
                    // 选择相册，单张
                    mPhotoSelector.getPhotoListFromSelector(1, null);
                    mPhotoPopupWindow.hide();
                })
                .item2ClickListener(() -> {
                    // 选择相机，拍照
                    mPhotoSelector.getPhotoFromCamera(null);
                    mPhotoPopupWindow.hide();
                })
                .bottomClickListener(() -> mPhotoPopupWindow.hide()).build();
        mPhotoPopupWindow.show();
    }

    /**
     * 初始化取消选择弹框
     */
    private void initCanclePopupWindow() {
        if (mCanclePopupWindow != null) {
            mCanclePopupWindow.show();
            return;
        }
        mCanclePopupWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.dynamic_send_cancel_hint))
                .item2Str(getString(R.string.determine))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item2ClickListener(() -> {
                    mCanclePopupWindow.hide();
                    getActivity().finish();
                })
                .bottomClickListener(() -> mCanclePopupWindow.hide()).build();
        mCanclePopupWindow.show();
    }

    protected void showLinkDialog(final LinkDialog dialog, final boolean isChange) {
        dialog.setListener(new LinkDialog.OnDialogClickListener() {
            @Override
            public void onConfirmButtonClick(String name, String url) {
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url)) {
                    ToastUtils.showToast(R.string.not_empty);
                } else {
                    if (!isChange) {
                        mRichTextView.insertLink(url, name);
                    } else {
                        mRichTextView.changeLink(url, name);
                    }
                    onCancelButtonClick();
                }
            }

            @Override
            public void onCancelButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getFragmentManager(), LinkDialog.Tag);
    }

    protected void showPictureClickDialog(final PictureHandleDialog dialog, CharSequence[] items) {

        dialog.setListener(new PictureHandleDialog.OnDialogClickListener() {
            @Override
            public void onDeleteButtonClick(Long id) {
                mRichTextView.deleteImageById(id);
                removeFromLocalCache(id);
            }

            @Override
            public void onReloadButtonClick(Long id) {
                mRichTextView.setImageReload(id);
                mPresenter.uploadPic(mFailedImages.get(id), id);
                mInsertedImages.put(id, mFailedImages.get(id));
                mFailedImages.remove(id);
            }
        });
        dialog.setItems(items);
        dialog.show(getFragmentManager(), PictureHandleDialog.Tag);
    }

    protected void removeFromLocalCache(long id) {
        if (mInsertedImages.containsKey(id)) {
            mInsertedImages.remove(id);
        } else if (mFailedImages.containsKey(id)) {
            mFailedImages.remove(id);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissPop(mPhotoPopupWindow);
        dismissPop(mCanclePopupWindow);
    }
}

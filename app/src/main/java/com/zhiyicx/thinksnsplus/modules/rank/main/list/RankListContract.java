package com.zhiyicx.thinksnsplus.modules.rank.main.list;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.RankIndexBean;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/22
 * @contact email:648129313@qq.com
 */

public interface RankListContract {

    interface View extends ITSListView<RankIndexBean, Presenter>{

    }

    interface Presenter extends ITSListPresenter<RankIndexBean>{

    }

    interface Repository {

    }
}

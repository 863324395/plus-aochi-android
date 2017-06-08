package com.zhiyicx.thinksnsplus.data.source.remote;

import com.zhiyicx.thinksnsplus.data.beans.RechargeSuccessBean;
import com.zhiyicx.thinksnsplus.data.beans.WalletConfigBean;
import com.zhiyicx.thinksnsplus.data.beans.WithdrawResultBean;
import com.zhiyicx.thinksnsplus.data.beans.WithdrawalsListBean;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_PAHT_WALLET_CONFIG;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PAHT_WALLET_RECHARGE_SUCCESS;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PAHT_WALLET_WITHDRAW;


/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/5/27
 * @Contact master.jungle68@gmail.com
 */
public interface WalletClient {

    @GET(APP_PAHT_WALLET_CONFIG)
    Observable<WalletConfigBean> getWalletConfig();

    @FormUrlEncoded
    @POST(APP_PAHT_WALLET_WITHDRAW)
    Observable<WithdrawResultBean> withdraw(@Field("value") int value, @Field("type") String type, @Field("account") String account);

    @GET(APP_PAHT_WALLET_WITHDRAW)
    Observable<List<WithdrawalsListBean>> getWithdrawList(@Query("limit") int limit, @Query("after") int after);

    @GET(APP_PAHT_WALLET_RECHARGE_SUCCESS)
    Observable<RechargeSuccessBean> rechargeSuccess(@Path("charge") String charge);
}

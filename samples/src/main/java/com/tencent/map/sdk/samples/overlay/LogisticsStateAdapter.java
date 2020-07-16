package com.tencent.map.sdk.samples.overlay;


import android.content.Context;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.map.sdk.samples.R;

import java.util.List;

public class LogisticsStateAdapter  extends BaseQuickAdapter<LogisticsStateBean, BaseViewHolder> {
    private Context context;
    private List<LogisticsStateBean> data;

    public LogisticsStateAdapter(Context context, int layoutResId, @Nullable List<LogisticsStateBean> data) {
        super(layoutResId, data);
        this.context = context;
        this.data = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, LogisticsStateBean item) {
        //获取物流信息和物流时间的字体颜色, 最新的一条物流数据字体为绿色
        int newInfoColor = context.getResources().getColor(helper.getLayoutPosition() == 0 ? R.color.green : R.color.gray);
        //当前item的索引==0 && 物流数据的数量大于1条   ->  显示绿色大圆圈
        helper.setGone(R.id.iv_new, helper.getLayoutPosition() == 0 && data.size() > 1)
                //当前item的索引!=0 && 物流数据的数量大于1条   ->  显示灰色小圆圈
                .setGone(R.id.iv_old, helper.getLayoutPosition() != 0 && data.size() > 1)
                //当前item的索引 != 0    ->  显示圆点上面短一点的灰线
                .setVisible(R.id.v_short_line, helper.getLayoutPosition() != 0)
                //当前item的索引 != 物流数据的最后一条    ->  显示圆点下面长一点的灰线
                .setGone(R.id.v_long_line, helper.getLayoutPosition() != data.size() - 1)
                //当前item的索引 != 物流数据的最后一条    ->  显示物流时间下面的横向的灰线
                .setGone(R.id.v_bottom_line, helper.getLayoutPosition() != data.size() - 1)
                .setTextColor(R.id.tv_info, newInfoColor)
                .setTextColor(R.id.tv_date, newInfoColor)
                //物流信息
                .setText(R.id.tv_info, item.getAcceptStation())
                //物流时间
                .setText(R.id.tv_date, item.getAcceptTime());
    }
}

class LogisticsStateBean{
    private String acceptTime;
    private String acceptStation;

    public LogisticsStateBean() {
    }

    public LogisticsStateBean(String acceptTime, String acceptStation) {
        this.acceptTime = acceptTime;
        this.acceptStation = acceptStation;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptStation() {
        return acceptStation;
    }

    public void setAcceptStation(String acceptStation) {
        this.acceptStation = acceptStation;
    }
}

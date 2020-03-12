package com.iqonic.shophop.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemRewardBinding
import com.iqonic.shophop.models.Reward
import com.iqonic.shophop.utils.extensions.color
import com.iqonic.shophop.utils.extensions.onClick
import com.iqonic.shophop.utils.scratch.ScratchTextView
import kotlinx.android.synthetic.main.activity_reward.*
import kotlinx.android.synthetic.main.dialog_reward.*
import kotlinx.android.synthetic.main.toolbar.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class RewardActivity : AppBaseActivity() {

    lateinit var mTvRewardLbl: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)

        setToolbar(toolbar)
        title = getString(R.string.title_reward)

        rewardEffect()

        btnInvite.onClick {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))
        }

        val mRewardAdapter = object : BaseRecyclerAdapter<Reward, ItemRewardBinding>() {
            override fun onItemLongClick(view: View, model: Reward, position: Int) {
            }

            override fun onItemClick(view: View, model: Reward, position: Int, dataBinding: ItemRewardBinding) {
                if (view.id == R.id.rlMainReward) {
                    initDialog()
                    mTvRewardLbl.text = model.rewardValue
                }
            }

            override val layoutResId: Int = R.layout.item_reward

            override fun onBindData(model: Reward, position: Int, dataBinding: ItemRewardBinding) {
                dataBinding.model = model
            }
        }

        mRewardAdapter.addItems(getItems())
        rvReward.adapter = mRewardAdapter
        mCardReward.onClick { rewardEffect() }
    }

    private fun initDialog() {
        val dialog = Dialog(this@RewardActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.dialog_reward)

        dialog.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)

        mTvRewardLbl = dialog.findViewById(R.id.tvRewardLbl)


        dialog.scratch_view.setRevealListener(object : ScratchTextView.IRevealListener {
            override fun onRevealPercentChangedListener(stv: ScratchTextView?, percent: Float) {

            }

            override fun onRevealed(tv: ScratchTextView) {
                Toast.makeText(this@RewardActivity, "Revealed!", Toast.LENGTH_SHORT).show()
            }
        })
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun getItems(): ArrayList<Reward> {
        val list = ArrayList<Reward>()
        val reward1 = Reward()
        reward1.reward = R.drawable.ic_app_icon
        reward1.rewardValue = "$100 Rewards"
        reward1.rewardImg = R.drawable.ic_logo_sidebar

        val product1 = Reward()
        product1.reward = R.drawable.ic_app_icon
        product1.rewardValue = "You won $500 rewards"
        product1.rewardImg = R.drawable.ic_logo_sidebar

        val product2 = Reward()
        product2.reward = R.drawable.ic_app_icon
        product2.rewardValue = "you won $500 "
        product2.rewardImg = R.drawable.ic_logo_sidebar

        val product3 = Reward()
        product3.reward = R.drawable.ic_app_icon
        product3.rewardValue = "you won $500 "
        product3.rewardImg = R.drawable.ic_logo_sidebar

        val product4 = Reward()
        product4.reward = R.drawable.ic_app_icon
        product4.rewardValue = "you won $500 "
        product4.rewardImg = R.drawable.ic_logo_sidebar

        list.add(reward1)
        list.add(product1)
        list.add(product2)
        list.add(product3)
        list.add(product4)
        return list
    }

    private fun rewardEffect() {
        viewKonfetti.build()
                .addColors(color(R.color.track_green), color(R.color.cat_4), color(R.color.colorPrimary), color(R.color.yellow))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size(12), Size(16, 6f))
                .setPosition(viewKonfetti.x + viewKonfetti.width / 2, viewKonfetti.y + viewKonfetti.height / 3)
                .burst(100)
    }
}

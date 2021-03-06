package org.wikiedufoundation.wikiedudashboard.ui.campaign.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.wikiedufoundation.wikiedudashboard.data.network.WikiEduDashboardApi
import org.wikiedufoundation.wikiedudashboard.ui.campaign.dao.ActiveCampaignDao
import org.wikiedufoundation.wikiedudashboard.ui.campaign.data.CampaignListData

/**Declares the DAO as a private property in the constructor. Pass in the DAO
 *instead of the whole database, because you only need access to the DAO*
 * */
class ActiveCampaignRepositoryImpl(
    private val wikiEduDashboardApi: WikiEduDashboardApi,
    private val activeCampaignDao: ActiveCampaignDao
) : ActiveCampaignRepository {

    /** Room executes all queries on a separate thread.
     * Observed LiveData will notify the observer when the data has changed.
     * */
    override fun allCapaignList(): LiveData<List<CampaignListData>> {
        return activeCampaignDao.getAllCampaign()
    }

    /** The suspend modifier tells the compiler that this must be called from a
     *  coroutine or another suspend function.
     **/
    override suspend fun requestCampaignList(cookies: String) {
        withContext(Dispatchers.Main) {
            val request = wikiEduDashboardApi.getExploreCampaigns(cookies)
            val campaignList = request.campaigns
            activeCampaignDao.insertCampaign(campaignList)
        }
    }
}
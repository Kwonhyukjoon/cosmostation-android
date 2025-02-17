package wannabit.io.cosmostaion.model.viewModel.neutron

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.launch
import wannabit.io.cosmostaion.base.chains.ChainConfig
import wannabit.io.cosmostaion.dao.Account
import wannabit.io.cosmostaion.model.repository.neutron.DaoRepository
import wannabit.io.cosmostaion.model.viewModel.BaseViewModel
import wannabit.io.cosmostaion.network.res.neutron.*
import kotlin.Pair

class DaoViewModel(private val daoRepository: DaoRepository) : BaseViewModel() {

    private var _daoListData = MutableLiveData<List<ResDaoData?>?>()
    val daoListData: LiveData<List<ResDaoData?>?> get() = _daoListData

    fun loadDaoListData(chainConfig: ChainConfig) = backScope.launch {
        try {
            daoRepository.getDaoData(chainConfig).let { response ->
                if (response.isSuccessful) {
                    _daoListData.postValue(response.body())
                } else {
                    _daoListData.postValue(null)
                }
            }
        } catch (_: Exception) { }
    }

    private var _daoProposalListData = MutableLiveData<List<Pair<String?, ProposalData?>>>()
    val daoProposalListData: LiveData<List<Pair<String?, ProposalData?>>> get() = _daoProposalListData

    fun loadDaoProposalListData(chainConfig: ChainConfig, contractAddressList: MutableList<String?>) = backScope.launch {
        try {
            val proposalMap = contractAddressList.flatMap { address ->
                val response = daoRepository.getDaoProposalListData(chainConfig, address)
                Gson().fromJson(response, ResProposalData::class.java).proposals.map { Pair(address, it) }
            }
            _daoProposalListData.postValue(proposalMap)
        } catch (_: Exception) { }
    }

    private var _daoMyVoteStatusData = MutableLiveData<List<ResMyVoteStatus>>()
    val daoMyVoteStatusData: LiveData<List<ResMyVoteStatus>> get() = _daoMyVoteStatusData

    fun loadDaoProposalMyVoteData(c: Context, chainConfig: ChainConfig, account: Account) = backScope.launch {
        try {
            daoRepository.getMyVoteStatus(c, chainConfig, account).let { response ->
                if (response.isSuccessful) {
                    _daoMyVoteStatusData.postValue(response.body())
                }
            }
        } catch (_: Exception) { }
    }

    private var _daoMemberStatus = MutableLiveData<ResMemberList?>()
    val daoMemberStatus: LiveData<ResMemberList?> get() = _daoMemberStatus

    fun loadMemberList(chainConfig: ChainConfig, contractAddress: String?) =  backScope.launch {
        try {
            daoRepository.getListMemberData(chainConfig, contractAddress)?.let { response ->
                if (response.isNotEmpty()) {
                    _daoMemberStatus.postValue(Gson().fromJson(response, ResMemberList::class.java))
                } else {
                    _daoMemberStatus.postValue(null)
                }
            }
        } catch (_: Exception) {
            _daoMemberStatus.postValue(null)
        }
    }
}
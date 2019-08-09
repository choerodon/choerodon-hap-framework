import { action, computed, observable } from 'mobx';
import { axios, store } from '@choerodon/boot';

class BoundStore {
  queryInbound = ((id = undefined) => axios.get(`/sys/invoke/querryInbound/${id}`));
  
  queryOutbound = ((id = undefined) => axios.get(`/sys/invoke/querryOutbound/${id}`));
}
const boundStore = new BoundStore();
export default boundStore;

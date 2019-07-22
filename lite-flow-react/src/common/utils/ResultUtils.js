const RESULT_DATA_PARAM = "data";
const RESULT_STATUS_PARAM = "status";

const RESULT_SUCCESS_STATUS = 0;

module.exports = {

    /**
     * 结果是否成功
     * @param result
     */
    isSuccess(result){
        if(result && result[RESULT_STATUS_PARAM] == RESULT_SUCCESS_STATUS){
            return true;
        }
        return false;
    },
    /**
     * 获取结果
     * @param result
     * @returns {*}
     */
    getData(result){
        if(result){
            return result[RESULT_DATA_PARAM];
        }
        return null;
    },
    /**
     * 直接获取结果
     * @param result
     * @returns {*}
     */
    getDataDirectly(result){
        if(this.isSuccess(result)){
            return this.getData(result);
        }
    }
}

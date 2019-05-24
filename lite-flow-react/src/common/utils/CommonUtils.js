module.exports = {
    dateFormat: (time) => {
        if (!time) {
            return ""
        }
        const datetime = new Date();
        datetime.setTime(time);
        const year = datetime.getFullYear();
        const month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
        const date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
        const hour = datetime.getHours() < 10 ? "0" + datetime.getHours() : datetime.getHours();
        const minute = datetime.getMinutes() < 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
        const second = datetime.getSeconds() < 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
        return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;
    },
    /**获取对象参数的值
     * @param key
     * @param model
     * @param defaultValue
     * @returns {*}
     */
    getValueFromModel(key, model, defaultValue) {
        /**
         * 为null时返回null
         */
        if(!model){
            return defaultValue;
        }
        if (key.indexOf(".") < 0) {
            if (model && model[key] != null) {
                return model[key];
            }
        }
        else {
            let keyArray = key.split(".");
            let temp = model;
            for (let i = 0; i < keyArray.length; i++) {
                let keyItem = keyArray[i];
                if (temp[keyItem] != null) {
                    temp = temp[keyItem];
                    if (i == keyArray.length - 1) {
                        return temp;
                    }
                } else {
                    break;
                }
            }

        }
        return defaultValue;
    },

    getStringValueFromModel(key, model, defaultValue) {
        return this.getValueFromModel(key, model, defaultValue) + "";
    },
    /**
     * 产生随机数函数
     * @param n
     * @returns {string}
     */
    randonNum: (n) => {
        let rnd = "";
        for (let i = 0; i < n; i++)
            rnd += Math.floor(Math.random() * 10);
        return rnd;
    },
    /**
     * 获取耗时
     * @param n
     * @returns {string}
     */
    getTimeConsumer: (startTime, endTime) => {
        if(startTime == null || endTime == null) {
            return "";
        }
        const second = 1000;
        const minute = 60 * second;
        const hour = 60 * minute;
        let time = endTime - startTime;

        if(time <= 0){
            return "0";
        }

        let result = "";
        /**
         * 小时
         * @type {number}
         */
        let hourTmp = time / hour;
        if(hourTmp >= 1){
            hourTmp = parseInt("" + hourTmp);
            result += hourTmp + "小时";
            time -= hourTmp * hour;
        }
        /**
         * 分钟
         */
        let minuteTmp = time / minute;
        if(minuteTmp >= 1){
            minuteTmp = parseInt("" + minuteTmp);
            result += minuteTmp + "分";
            time -= minuteTmp * minute;
        }
        /**
         * 秒
         */
        let secondTmp = time / second;
        if(secondTmp >= 1){
            secondTmp = parseInt("" + secondTmp);
            result += secondTmp + "秒";
        }
        return result;

    }
}

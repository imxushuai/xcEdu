import http from './../../../base/api/public'
import querystring from 'querystring'
let sysConfig = require('@/../config/sysConfig')
let apiUrl = sysConfig.xcApiUrlPre;
/**
 * 分页查询CMS页面
 * 
 * @param {*} page 当前页
 * @param {*} size 每页记录数
 * @param {*} params 其他参数：页面别名、站点ID、页面ID等
 */
export const page_list = (page,size,params) => { 
    let queryParams = querystring.stringify(params)
    return http.requestQuickGet(apiUrl + '/cms/page/list/'+page+'/'+size + '?' + queryParams) 
}

/**
 * 查询所有站点信息
 */
export const site_list_all = () => { 
    return http.requestQuickGet(apiUrl + '/cms/site') 
}
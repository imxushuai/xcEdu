import http from './../../../base/api/public'
import querystring from 'querystring'
let sysConfig = require('@/../config/sysConfig')
let apiUrl = sysConfig.xcApiUrlPre;

/********************************************
 * CMS页面相关API
 ********************************************/
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
 * 新增页面
 */
export const add = (params) => { 
    return http.requestPost(apiUrl + '/cms/page', params)
}

/**
 * 修改页面
 */
export const edit = (params) => { 
    return http.requestPut(apiUrl + '/cms/page', params)
}

/**
 * 按ID查询页面
 */
export const findCmsPageById = (pageId) => { 
    return http.requestQuickGet(apiUrl + '/cms/page/'+ pageId)
}

/**
 * 按ID删除页面
 */
export const deleteCmsPageById = (pageId) => { 
    return http.requestDelete(apiUrl + '/cms/page/'+ pageId)
}


/********************************************
 * CMS站点相关API
 ********************************************/
/**
 * 分页查询CMS站点
 */ 
export const site_list = (page,size) => {
    return http.requestQuickGet(apiUrl + '/cms/site/list/'+page+'/'+size) 
}

/**
 * 查询所有站点信息
 */
export const site_list_all = () => { 
    return http.requestQuickGet(apiUrl + '/cms/site') 
}

/**
 * 新增站点
 */
export const addCmsSite = (params) => { 
    return http.requestPost(apiUrl + '/cms/site', params)
}

/**
 * 修改站点
 */
export const editCmsSite = (params) => { 
    return http.requestPut(apiUrl + '/cms/site', params)
}

/**
 * 按ID查询站点
 */
export const findCmsSiteById = (pageId) => { 
    return http.requestQuickGet(apiUrl + '/cms/site/'+ pageId)
}

/**
 * 按ID删除站点
 */
export const deleteCmsSiteById = (pageId) => { 
    return http.requestDelete(apiUrl + '/cms/site/'+ pageId)
}

/********************************************
 * CMS模板相关API
 ********************************************/
/**
 * 分页查询CMS模板
 */ 
export const template_list = (page,size) => {
    return http.requestQuickGet(apiUrl + '/cms/template/list/'+page+'/'+size) 
} 
/**
 * 查询所有模板信息
 */
export const template_list_all = () => { 
    return http.requestQuickGet(apiUrl + '/cms/template') 
}

/**
 * 新增模板
 */
export const addCmsTemplate = (params) => { 
    return http.requestPost(apiUrl + '/cms/template', params)
}

/**
 * 修改模板
 */
export const editCmsTemplate = (params) => { 
    return http.requestPut(apiUrl + '/cms/template', params)
}

/**
 * 按ID查询模板
 */
export const findCmsTemplateById = (pageId) => { 
    return http.requestQuickGet(apiUrl + '/cms/template/'+ pageId)
}

/**
 * 按ID删除模板
 */
export const deleteCmsTemplateById = (pageId) => { 
    return http.requestDelete(apiUrl + '/cms/template/'+ pageId)
}
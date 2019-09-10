import http from './../../../base/api/public'
export const page_list = (page,size,params) => { 
    return http.requestQuickGet('http://localhost:31001/cms/page/list/'+page+'/'+size) 
}





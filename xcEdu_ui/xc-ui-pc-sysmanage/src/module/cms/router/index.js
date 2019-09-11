import Home from '@/module/home/page/home.vue'
import page_list from '@/module/cms/page/page_list.vue'
import page_add from '@/module/cms/page/page_add.vue'
import page_edit from '@/module/cms/page/page_edit.vue'
import site_list from '@/module/cms/page/site_list.vue'
import site_add from '@/module/cms/page/site_add.vue'
import site_edit from '@/module/cms/page/site_edit.vue'
import template_list from '@/module/cms/page/template_list.vue'
import template_add from '@/module/cms/page/template_add.vue'
import template_edit from '@/module/cms/page/template_edit.vue'

export default [{ 
  path: '/cms', 
  component: Home, 
  name: 'CMS内容管理', 
  hidden: false, 
  children:[{
    path:'/cms/page/list',
    name:'页面列表',
    component: page_list,
    hidden:false
  },{
    path:'/cms/page/add',
    name:'页面新增',
    component: page_add,
    hidden:true
  },{
    path:'/cms/page/edit',
    name:'页面修改',
    component: page_edit,
    hidden:true
  },{
    path:'/cms/site/list',
    name:'站点列表',
    component: site_list,
    hidden:false
  },{
    path:'/cms/site/add',
    name:'站点新增',
    component: site_add,
    hidden:true
  },{
    path:'/cms/site/edit',
    name:'站点修改',
    component: site_edit,
    hidden:true
  },{
    path:'/cms/template/list',
    name:'模板列表',
    component: template_list,
    hidden:false
  },{
    path:'/cms/template/add',
    name:'模板新增',
    component: template_add,
    hidden:true
  },{
    path:'/cms/template/edit',
    name:'模板修改',
    component: template_edit,
    hidden:true
  }]
}]
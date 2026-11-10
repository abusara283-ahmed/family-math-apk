/* أسرة الرياضيات – طبقة توافق داخل تطبيق APK: حفظ الملفات والطباعة */
(function(){if(window.__apk)return;window.__apk=1;
var S=null;try{S=window.AndroidSave||(window.parent&&window.parent.AndroidSave)||null}catch(e){}
if(!S)return;
var M={},oco=URL.createObjectURL;
URL.createObjectURL=function(b){var u=oco.apply(URL,arguments);try{if(b instanceof Blob)M[u]=b}catch(e){}return u};
function viaBlob(b,name){var r=new FileReader();r.onload=function(){S.save(name,String(r.result).split(',')[1]||'',b.type||'')};r.readAsDataURL(b)}
function save(href,name){
 if(/^data:/.test(href)){var m=href.match(/^data:([^;,]*)((?:;[^;,]*)*),([\s\S]*)$/);if(!m)return;
  var d=/;base64/.test(m[2])?m[3]:btoa(unescape(encodeURIComponent(decodeURIComponent(m[3]))));S.save(name,d,m[1]||'');return}
 if(M[href]){viaBlob(M[href],name);return}
 fetch(href).then(function(r){return r.blob()}).then(function(b){viaBlob(b,name)}).catch(function(){})}
function handle(a){var h=a.getAttribute('href')||'';if(!a.hasAttribute('download')||!/^(blob|data):/.test(h))return false;
 save(a.href,a.getAttribute('download')||'ملف');return true}
document.addEventListener('click',function(e){var a=e.target&&e.target.closest&&e.target.closest('a[download]');
 if(a&&handle(a)){e.preventDefault();e.stopImmediatePropagation()}},true);
var oc=HTMLAnchorElement.prototype.click;
HTMLAnchorElement.prototype.click=function(){if(handle(this))return;return oc.apply(this,arguments)};
window.print=function(){try{
 var src=document.documentElement,c=src.cloneNode(true);
 var a=src.querySelectorAll('input,textarea,select'),b=c.querySelectorAll('input,textarea,select');
 for(var i=0;i<a.length&&i<b.length;i++){var x=a[i],y=b[i];
  if(x.tagName==='TEXTAREA')y.textContent=x.value;
  else if(x.tagName==='SELECT'){for(var j=0;j<x.options.length;j++)if(x.options[j].selected)y.options[j].setAttribute('selected','')}
  else if(x.type==='checkbox'||x.type==='radio'){if(x.checked)y.setAttribute('checked','');else y.removeAttribute('checked')}
  else y.setAttribute('value',x.value)}
 var ca=src.querySelectorAll('canvas'),cb=c.querySelectorAll('canvas');
 for(var k=0;k<ca.length&&k<cb.length;k++){try{var im=document.createElement('img');im.src=ca[k].toDataURL();im.style.cssText=ca[k].style.cssText;im.width=ca[k].width;im.height=ca[k].height;cb[k].parentNode.replaceChild(im,cb[k])}catch(e){}}
 var sc=c.querySelectorAll('script');for(var s=0;s<sc.length;s++)sc[s].remove();
 S.print('<!DOCTYPE html>'+c.outerHTML,document.title||'')}catch(e){}};
})();

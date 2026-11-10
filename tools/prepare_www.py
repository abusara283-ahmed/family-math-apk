#!/usr/bin/env python3
# يحوّل حزمة تطبيق PWA لأسرة الرياضيات إلى مجلد www لتطبيق APK.
# الاستخدام: python3 tools/prepare_www.py <تطبيق-أسرة-الرياضيات-للتثبيت-على-أندرويد.zip>
import sys,os,zipfile,json
here=os.path.dirname(os.path.abspath(__file__)); root=os.path.dirname(here); www=os.path.join(root,'www')
z=zipfile.ZipFile(sys.argv[1]); n=[x for x in z.namelist() if x.endswith('/index.html')][0]; d=n.rsplit('/',1)[0]
s=z.read(n).decode('utf-8')
shim=open(os.path.join(here,'apk-shim.js'),encoding='utf-8').read()
def rep(a,b):
    global s
    assert s.count(a)==1,('patch not found',a[:60]); s=s.replace(a,b)
rep(', IS_PWA=true;',', IS_PWA=false;')
rep("var nm=asciiName(p.split('/').pop()),u;","var nm=asciiName(p.split('/').pop()),u;\n if(window.AndroidSave){AndroidSave.save(nm,f[1],'application/zip');return}")
rep('function toast(t){','var APKSHIM='+json.dumps(shim,ensure_ascii=False).replace('</','<\\/')+';\nINJ+="<script>"+APKSHIM+"<\\/script>";try{(new Function(APKSHIM))()}catch(e){}\nfunction toast(t){')
rep('</style>','#inst,#howInst{display:none!important}</style>') if s.count('</style>')==1 else None
if '#inst,#howInst{display:none' not in s:
    i=s.index('</head>'); s=s[:i]+'<style>#inst,#howInst{display:none!important}</style>'+s[i:]
os.makedirs(www,exist_ok=True)
open(os.path.join(www,'index.html'),'w',encoding='utf-8').write(s)
for f in ['icon-192.png','icon-512.png','icon-maskable-512.png','manifest.webmanifest']:
    open(os.path.join(www,f),'wb').write(z.read(d+'/'+f))
v=z.read(d+'/sw.js').decode(); import re
print('www جاهز من الإصدار',re.search(r"var V='([^']*)'",v).group(1))

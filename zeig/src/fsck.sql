select nm.id,pt_nm.v
	from nm,ns,pt pt_nm,pt pt_ns
	where nm.ns=ns.id
	and ns.uri=pt_ns.id
	and pt_ns.v=''
	and nm.v=pt_nm.id
	and (pt_nm.v='#root'
		or pt_nm.v='#node'
		or pt_nm.v='#processing-instruction');

select xt.id
	from xt
	left join xn on xn.v=xt.id and (xn.nm=3 or xt.nm=6)
	left join ve on xt.id=ve.v
	where xn.v is null and ve.v is null;
package com.yangc.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yangc.dao.BaseDao;
import com.yangc.system.bean.TSysPerson;
import com.yangc.system.service.PersonService;
import com.yangc.utils.lang.PinyinUtils;

@Service
@SuppressWarnings("unchecked")
public class PersonServiceImpl implements PersonService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public void addOrUpdatePerson(TSysPerson person) {
		person.setSpell(PinyinUtils.getPinyin(person.getName()) + " " + PinyinUtils.getPinyinHead(person.getName()));
		this.baseDao.saveOrUpdate(person);
	}

	@Override
	public void delPersonByUserId(Long userId) {
		this.baseDao.updateOrDelete("delete TSysPerson where userId = ?", new Object[] { userId });
	}

	@Override
	public TSysPerson getPersonByUserId(Long userId) {
		return (TSysPerson) this.baseDao.get("from TSysPerson where userId = ?", new Object[] { userId });
	}

	@Override
	public List<TSysPerson> getPersonList(String condition) {
		if (StringUtils.isNotBlank(condition)) {
			String hql = "select new TSysPerson(name, spell) from TSysPerson where name like :condition or spell like :condition";
			Map<String, Object> paramMap = new HashMap<String, Object>(1);
			paramMap.put("condition", "%" + condition + "%");
			return this.baseDao.findAllByMap(hql, paramMap);
		}
		return null;
	}

	@Override
	public List<TSysPerson> getPersonListByPersonNameAndDeptId_page(String personName, Long deptId) {
		StringBuilder sb = new StringBuilder();
		sb.append("select new TSysPerson(p.id, p.name, p.sex, p.phone, p.spell, u.id as userId, u.username, p.deptId, d.deptName)");
		sb.append(" from TSysPerson p, TSysUser u, TSysDepartment d where p.userId = u.id and p.deptId = d.id");
		Map<String, Object> paramMap = new HashMap<String, Object>();

		if (StringUtils.isNotBlank(personName)) {
			sb.append(" and p.name = :personName");
			paramMap.put("personName", personName);
		}
		if (deptId != null && deptId.longValue() != 0) {
			sb.append(" and p.deptId = :deptId");
			paramMap.put("deptId", deptId);
		}
		sb.append(" order by p.id");

		return this.baseDao.findByMap(sb.toString(), paramMap);
	}

	@Override
	public Long getPersonListByPersonNameAndDeptId_count(String personName, Long deptId) {
		StringBuilder sb = new StringBuilder("select count(p) from TSysPerson p where 1 = 1");
		Map<String, Object> paramMap = new HashMap<String, Object>();

		if (StringUtils.isNotBlank(personName)) {
			sb.append(" and p.name = :personName");
			paramMap.put("personName", personName);
		}
		if (deptId != null && deptId.longValue() != 0) {
			sb.append(" and p.deptId = :deptId");
			paramMap.put("deptId", deptId);
		}

		Number count = (Number) this.baseDao.findAllByMap(sb.toString(), paramMap).get(0);
		return count.longValue();
	}

}

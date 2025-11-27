package org.example.smartshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.PromoCodeRequest;
import org.example.smartshop.dto.response.PromoCodeResponse;
import org.example.smartshop.entity.PromoCode;
import org.example.smartshop.exception.BusinessException;
import org.example.smartshop.exception.ResourceNotFoundException;
import org.example.smartshop.mapper.PromoCodeMapper;
import org.example.smartshop.repository.PromoCodeRepository;
import org.example.smartshop.service.IPromoCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoCodeServiceImpl implements IPromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;

    @Override
    public PromoCodeResponse createPromoCode(PromoCodeRequest request) {
        if (promoCodeRepository.findByCode(request.getCode()).isPresent()) {
            throw new BusinessException("Un code promo avec ce code existe deja: " + request.getCode());
        }

        PromoCode promoCode = promoCodeMapper.toEntity(request);
        PromoCode savedPromoCode = promoCodeRepository.save(promoCode);

        return promoCodeMapper.toResponse(savedPromoCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeResponse> getAllPromoCodes() {
        return promoCodeRepository.findAll().stream()
                .map(promoCodeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePromoCode(Long id) {
        if (!promoCodeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Code promo non trouve avec l'ID: " + id);
        }
        promoCodeRepository.deleteById(id);
    }
}

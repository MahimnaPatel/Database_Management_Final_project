package uga.menik.csx370.controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Lot;
import uga.menik.csx370.services.LotService;

@Controller
@RequestMapping("/lots")
public class LotsController {

    private final LotService lotService;

    @Autowired
    public LotsController(LotService lotService) {
        this.lotService = lotService;
    }

    @GetMapping
    public ModelAndView page(@RequestParam(name = "q", required = false) String query,
                             @RequestParam(name = "paymentType", required = false) String paymentType) throws SQLException {
        List<Lot> lots = lotService.searchLots(query, paymentType);
        ModelAndView mv = new ModelAndView("lots_page");
        mv.addObject("lots", lots);
        mv.addObject("q", query == null ? "" : query);
        mv.addObject("paymentType", paymentType == null ? "" : paymentType);
        mv.addObject("isParkMobile", "ParkMobile".equals(paymentType));
        mv.addObject("isFree", "Free".equals(paymentType));
        mv.addObject("isFreeAfter5PM", "Free_After_5PM".equals(paymentType));
        return mv;
    }

    private void addPaymentTypeFlags(ModelAndView mv, String paymentType) {
        mv.addObject("isParkMobile", "ParkMobile".equals(paymentType));
        mv.addObject("isFree", "Free".equals(paymentType));
        mv.addObject("isFreeAfter5PM", "Free_After_5PM".equals(paymentType));
    }

    @GetMapping("/add")
    public ModelAndView addForm() {
        ModelAndView mv = new ModelAndView("lot_form_page");
        mv.addObject("pageTitle", "Add New Lot");
        mv.addObject("actionUrl", "/lots/add");
        mv.addObject("isEdit", false);
        mv.addObject("lot", new Lot(0, "", "", 0, "ParkMobile", 0.0, 0.0));
        addPaymentTypeFlags(mv, "ParkMobile");
        return mv;
    }

    @PostMapping("/add")
    public ModelAndView addLot(@RequestParam String name,
                               @RequestParam String address,
                               @RequestParam int totalCapacity,
                               @RequestParam String paymentType,
                               @RequestParam double latitude,
                               @RequestParam double longitude) throws SQLException {
        ModelAndView mv;
        if (name == null || name.trim().isEmpty() || address == null || address.trim().isEmpty()) {
            mv = new ModelAndView("lot_form_page");
            mv.addObject("errorMessage", "Name and address are required.");
            mv.addObject("pageTitle", "Add New Lot");
            mv.addObject("actionUrl", "/lots/add");
            mv.addObject("isEdit", false);
            mv.addObject("lot", new Lot(0, name, address, totalCapacity, paymentType, latitude, longitude));
            addPaymentTypeFlags(mv, paymentType);
            return mv;
        }

        lotService.createLot(name.trim(), address.trim(), totalCapacity, paymentType, latitude, longitude);
        mv = new ModelAndView("redirect:/lots");
        return mv;
    }

    @GetMapping("/{lotId}")
    public ModelAndView detail(@PathVariable int lotId) throws SQLException {
        Optional<Lot> lot = lotService.getLotById(lotId);
        if (lot.isEmpty()) {
            ModelAndView mv = new ModelAndView("lots_page");
            mv.addObject("errorMessage", "Lot not found.");
            mv.addObject("lots", lotService.searchLots(null, null));
            mv.addObject("q", "");
            mv.addObject("paymentType", "");
            return mv;
        }

        ModelAndView mv = new ModelAndView("lot_detail_page");
        mv.addObject("lot", lot.get());
        return mv;
    }

    @GetMapping("/{lotId}/edit")
    public ModelAndView editForm(@PathVariable int lotId) throws SQLException {
        Optional<Lot> lot = lotService.getLotById(lotId);
        if (lot.isEmpty()) {
            ModelAndView mv = new ModelAndView("redirect:/lots");
            return mv;
        }

        ModelAndView mv = new ModelAndView("lot_form_page");
        mv.addObject("pageTitle", "Edit Lot");
        mv.addObject("actionUrl", "/lots/" + lotId + "/edit");
        mv.addObject("isEdit", true);
        mv.addObject("lot", lot.get());
        addPaymentTypeFlags(mv, lot.get().getPaymentType());
        return mv;
    }

    @PostMapping("/{lotId}/edit")
    public ModelAndView editLot(@PathVariable int lotId,
                                @RequestParam String name,
                                @RequestParam String address,
                                @RequestParam int totalCapacity,
                                @RequestParam String paymentType,
                                @RequestParam double latitude,
                                @RequestParam double longitude) throws SQLException {
        if (name == null || name.trim().isEmpty() || address == null || address.trim().isEmpty()) {
            ModelAndView mv = new ModelAndView("lot_form_page");
            mv.addObject("errorMessage", "Name and address are required.");
            mv.addObject("pageTitle", "Edit Lot");
            mv.addObject("actionUrl", "/lots/" + lotId + "/edit");
            mv.addObject("isEdit", true);
            mv.addObject("lot", new Lot(lotId, name, address, totalCapacity, paymentType, latitude, longitude));
            addPaymentTypeFlags(mv, paymentType);
            return mv;
        }

        lotService.updateLot(lotId, name.trim(), address.trim(), totalCapacity, paymentType, latitude, longitude);
        return new ModelAndView("redirect:/lots/" + lotId);
    }

    @PostMapping("/{lotId}/delete")
    public ModelAndView deleteLot(@PathVariable int lotId) throws SQLException {
        lotService.deleteLot(lotId);
        return new ModelAndView("redirect:/lots");
    }
}

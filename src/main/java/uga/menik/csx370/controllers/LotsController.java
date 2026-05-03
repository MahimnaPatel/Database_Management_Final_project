package uga.menik.csx370.controllers;

import java.sql.SQLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
                             @RequestParam(name = "paymentType", required = false) String paymentType,
                             @RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("lots_page");
        try {
            List<Lot> lots = lotService.searchLots(query, paymentType);
            mv.addObject("lots", lots);
            mv.addObject("q", query == null ? "" : query);
            mv.addObject("paymentType", paymentType == null ? "" : paymentType);
            mv.addObject("errorMessage", error);
            addPaymentTypeFlags(mv, paymentType);
        } catch (SQLException e) {
            mv.addObject("errorMessage", "Database error: Unable to load lots. Please check database connection.");
            mv.addObject("lots", new ArrayList<>());
            mv.addObject("q", "");
            mv.addObject("paymentType", "");
            addPaymentTypeFlags(mv, "");
        }
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
                               @RequestParam(required = false) Double latitude,
                               @RequestParam(required = false) Double longitude) {
        ModelAndView mv;
        double safeLatitude = latitude == null ? 0.0 : latitude;
        double safeLongitude = longitude == null ? 0.0 : longitude;

        if (name == null || name.trim().isEmpty() || address == null || address.trim().isEmpty()
                || totalCapacity < 0 || !isValidPaymentType(paymentType)) {
            mv = new ModelAndView("lot_form_page");
            mv.addObject("errorMessage", "Name, address, non-negative capacity, and a valid payment type are required.");
            mv.addObject("pageTitle", "Add New Lot");
            mv.addObject("actionUrl", "/lots/add");
            mv.addObject("isEdit", false);
            mv.addObject("lot", new Lot(0, name, address, totalCapacity, paymentType, safeLatitude, safeLongitude, null, false, null));
            addPaymentTypeFlags(mv, paymentType);
            return mv;
        }

        try {
            lotService.createLot(name.trim(), address.trim(), totalCapacity, paymentType, safeLatitude, safeLongitude);
            mv = new ModelAndView("redirect:/lots");
        } catch (SQLException e) {
            mv = new ModelAndView("lot_form_page");
            mv.addObject("errorMessage", "Database error: Unable to add lot. Please try again.");
            mv.addObject("pageTitle", "Add New Lot");
            mv.addObject("actionUrl", "/lots/add");
            mv.addObject("isEdit", false);
            mv.addObject("lot", new Lot(0, name, address, totalCapacity, paymentType, safeLatitude, safeLongitude, null, false, null));
            addPaymentTypeFlags(mv, paymentType);
        }
        return mv;
    }

    @GetMapping("/{lotId}")
    public ModelAndView detail(@PathVariable int lotId) {
        try {
            Optional<Lot> lot = lotService.getLotById(lotId);
            if (lot.isEmpty()) {
                ModelAndView mv = new ModelAndView("lots_page");
                mv.addObject("errorMessage", "Lot not found.");
                mv.addObject("lots", new ArrayList<>());
                mv.addObject("q", "");
                mv.addObject("paymentType", "");
                addPaymentTypeFlags(mv, "");
                return mv;
            }

            ModelAndView mv = new ModelAndView("lot_detail_page");
            mv.addObject("lot", lot.get());
            return mv;
        } catch (SQLException e) {
            ModelAndView mv = new ModelAndView("lots_page");
            mv.addObject("errorMessage", "Database error: Unable to load lot details.");
            mv.addObject("lots", new ArrayList<>());
            mv.addObject("q", "");
            mv.addObject("paymentType", "");
            addPaymentTypeFlags(mv, "");
            return mv;
        }
    }

    @GetMapping("/{lotId}/edit")
    public ModelAndView editForm(@PathVariable int lotId) {
        try {
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
        } catch (SQLException e) {
            ModelAndView mv = new ModelAndView("redirect:/lots");
            return mv;
        }
    }

    @PostMapping("/{lotId}/edit")
    public ModelAndView editLot(@PathVariable int lotId,
                                @RequestParam String name,
                                @RequestParam String address,
                                @RequestParam int totalCapacity,
                                @RequestParam String paymentType,
                                @RequestParam(required = false) Double latitude,
                                @RequestParam(required = false) Double longitude) {
        double safeLatitude = latitude == null ? 0.0 : latitude;
        double safeLongitude = longitude == null ? 0.0 : longitude;

        if (name == null || name.trim().isEmpty() || address == null || address.trim().isEmpty()
                || totalCapacity < 0 || !isValidPaymentType(paymentType)) {
            ModelAndView mv = new ModelAndView("lot_form_page");
            mv.addObject("errorMessage", "Name, address, non-negative capacity, and a valid payment type are required.");
            mv.addObject("pageTitle", "Edit Lot");
            mv.addObject("actionUrl", "/lots/" + lotId + "/edit");
            mv.addObject("isEdit", true);
            mv.addObject("lot", new Lot(lotId, name, address, totalCapacity, paymentType, safeLatitude, safeLongitude, null, false, null));
            addPaymentTypeFlags(mv, paymentType);
            return mv;
        }

        try {
            lotService.updateLot(lotId, name.trim(), address.trim(), totalCapacity, paymentType, safeLatitude, safeLongitude);
            return new ModelAndView("redirect:/lots");
        } catch (SQLException e) {
            ModelAndView mv = new ModelAndView("lot_form_page");
            mv.addObject("errorMessage", "Database error: Unable to update lot. Please try again.");
            mv.addObject("pageTitle", "Edit Lot");
            mv.addObject("actionUrl", "/lots/" + lotId + "/edit");
            mv.addObject("isEdit", true);
            mv.addObject("lot", new Lot(lotId, name, address, totalCapacity, paymentType, safeLatitude, safeLongitude, null, false, null));
            addPaymentTypeFlags(mv, paymentType);
            return mv;
        }
    }

    @PostMapping("/{lotId}/delete")
    public ModelAndView deleteLot(@PathVariable int lotId) {
        try {
            lotService.deleteLot(lotId);
            return new ModelAndView("redirect:/lots");
        } catch (SQLException e) {
            return redirectToLotsWithError("Database error: Unable to delete lot. Please try again.");
        }
    }

    private boolean isValidPaymentType(String paymentType) {
        return "ParkMobile".equals(paymentType)
                || "Free".equals(paymentType)
                || "Free_After_5PM".equals(paymentType);
    }

    private ModelAndView redirectToLotsWithError(String message) {
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        return new ModelAndView("redirect:/lots?error=" + encoded);
    }
}
